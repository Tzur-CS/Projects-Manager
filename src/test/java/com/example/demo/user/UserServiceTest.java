package com.example.demo.user;

import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User(1L, "johndoe", "test-cognito-sub-123", User.UserRole.USER);
    }

    @Test
    void getAllUsers_ShouldReturnUserList() {
        // Arrange
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("johndoe", result.get(0).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = userService.getUserById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("johndoe", result.get().getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.getUserById(1L);

        // Assert
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserByUsername_ShouldReturnUser_WhenUserExists() {
        // Arrange
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = userService.getUserByUsername("johndoe");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("johndoe", result.get().getUsername());
        verify(userRepository, times(1)).findByUsername("johndoe");
    }

    @Test
    void createUser_ShouldCreateUser_WhenUsernameDoesNotExist() {
        // Arrange
        User newUser = new User(null, "newuser", "new-cognito-sub", User.UserRole.USER);
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.createUser(newUser);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).existsByUsername("newuser");
        verify(userRepository, times(1)).save(newUser);
    }

    @Test
    void createUser_ShouldThrowException_WhenUsernameExists() {
        // Arrange
        User newUser = new User(null, "johndoe", "new-cognito-sub", User.UserRole.USER);
        when(userRepository.existsByUsername("johndoe")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> userService.createUser(newUser));
        verify(userRepository, times(1)).existsByUsername("johndoe");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_ShouldUpdateUser_WhenUserExists() {
        // Arrange
        User updatedDetails = new User(null, "updateduser", null, User.UserRole.ADMIN);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.updateUser(1L, updatedDetails);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        User updatedDetails = new User(null, "updateduser", null, User.UserRole.ADMIN);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(1L, updatedDetails));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_ShouldDeleteUser_WhenUserExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(testUser);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1L));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).delete(any(User.class));
    }

}