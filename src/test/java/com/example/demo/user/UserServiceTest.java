package com.example.demo.user;

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
        testUser = new User(1L, "John Doe", "john@example.com", "1234567890", "johndoe", "test-cognito-sub-123", User.UserRole.USER);
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
        assertEquals("John Doe", result.get(0).getName());
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
        assertEquals("John Doe", result.get().getName());
        assertEquals("john@example.com", result.get().getEmail());
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
    void getUserByEmail_ShouldReturnUser_WhenEmailExists() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = userService.getUserByEmail("john@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
        verify(userRepository, times(1)).findByEmail("john@example.com");
    }

    @Test
    void createUser_ShouldSaveUser_WhenEmailDoesNotExist() {
        // Arrange
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.createUser(testUser);

        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
        verify(userRepository, times(1)).existsByEmail(testUser.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailExists() {
        // Arrange
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(testUser);
        });

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, times(1)).existsByEmail(testUser.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_ShouldUpdateUser_WhenUserExists() {
        // Arrange
        User updatedDetails = new User(null, "Jane Doe", "jane@example.com", "0987654321", "janedoe", "test-cognito-sub-456", User.UserRole.USER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.updateUser(1L, updatedDetails);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        User updatedDetails = new User(null, "Jane Doe", "jane@example.com", "0987654321", "janedoe", "test-cognito-sub-456", User.UserRole.USER);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(1L, updatedDetails);
        });

        assertTrue(exception.getMessage().contains("User not found"));
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
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteUser(1L);
        });

        assertTrue(exception.getMessage().contains("User not found"));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).delete(any(User.class));
    }
}

