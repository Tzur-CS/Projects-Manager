package com.example.demo.user;

import com.example.demo.security.AuthorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive unit tests for UserController
 * Tests the updated User entity with username, cognitoSub, and role fields
 */
@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthorizationService authorizationService;

    private User regularUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        // Regular user with USER role
        regularUser = new User(1L, "johndoe", "cognito-sub-123", User.UserRole.USER);

        // Admin user with ADMIN role
        adminUser = new User(2L, "admin", "cognito-sub-456", User.UserRole.ADMIN);
    }

    // ==================== GET /api/users - Get All Users (Admin Only) ====================

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_ShouldReturnUserList_WhenCalledByAdmin() throws Exception {
        // Arrange
        doNothing().when(authorizationService).requireAdmin(any());
        List<User> users = Arrays.asList(regularUser, adminUser);
        when(userService.getAllUsers()).thenReturn(users);

        // Act & Assert
        mockMvc.perform(get("/api/users").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("johndoe"))
                .andExpect(jsonPath("$[0].cognitoSub").value("cognito-sub-123"))
                .andExpect(jsonPath("$[0].role").value("USER"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].username").value("admin"))
                .andExpect(jsonPath("$[1].role").value("ADMIN"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_ShouldReturnEmptyList_WhenNoUsersExist() throws Exception {
        // Arrange
        doNothing().when(authorizationService).requireAdmin(any());
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/users").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ==================== GET /api/users/{id} - Get User by ID (Admin Only) ====================

    @Test
    @WithMockUser(roles = "admin")
    void getUserById_ShouldReturnUser_WhenUserExists() throws Exception {
        // Arrange
        doNothing().when(authorizationService).requireAdmin(any());
        when(userService.getUserById(1L)).thenReturn(Optional.of(regularUser));

        // Act & Assert
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.cognitoSub").value("cognito-sub-123"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @WithMockUser(roles = "admin")
    void getUserById_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        // Arrange
        doNothing().when(authorizationService).requireAdmin(any());
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "admin")
    void getUserById_ShouldReturnAdminUser_WhenAdminUserExists() throws Exception {
        // Arrange
        doNothing().when(authorizationService).requireAdmin(any());
        when(userService.getUserById(2L)).thenReturn(Optional.of(adminUser));

        // Act & Assert
        mockMvc.perform(get("/api/users/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }


    // ==================== User Entity Validation Tests ====================

    @Test
    @WithMockUser(roles = "admin")
    void getUserById_ShouldReturnUserWithoutEmailAndPhone() throws Exception {
        // Arrange - Verify user has no email or phone fields
        doNothing().when(authorizationService).requireAdmin(any());
        when(userService.getUserById(1L)).thenReturn(Optional.of(regularUser));

        // Act & Assert
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").exists())
                .andExpect(jsonPath("$.cognitoSub").exists())
                .andExpect(jsonPath("$.role").exists())
                .andExpect(jsonPath("$.email").doesNotExist())
                .andExpect(jsonPath("$.phone").doesNotExist())
                .andExpect(jsonPath("$.name").doesNotExist());
    }

    @Test
    @WithMockUser(roles = "admin")
    void getAllUsers_ShouldReturnOnlyUsernameAndRoleFields() throws Exception {
        // Arrange
        doNothing().when(authorizationService).requireAdmin(any());
        when(userService.getAllUsers()).thenReturn(Arrays.asList(regularUser));

        // Act & Assert - Verify structure has only username, cognitoSub, role
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("johndoe"))
                .andExpect(jsonPath("$[0].cognitoSub").value("cognito-sub-123"))
                .andExpect(jsonPath("$[0].role").value("USER"))
                .andExpect(jsonPath("$[0].email").doesNotExist())
                .andExpect(jsonPath("$[0].phone").doesNotExist());
    }

    // ==================== Role-Based Tests ====================

    @Test
    @WithMockUser(roles = "admin")
    void getAllUsers_ShouldReturnUsersWithDifferentRoles() throws Exception {
        // Arrange - Mix of USER and ADMIN roles
        doNothing().when(authorizationService).requireAdmin(any());
        User user1 = new User(1L, "user1", "sub-1", User.UserRole.USER);
        User user2 = new User(2L, "admin1", "sub-2", User.UserRole.ADMIN);
        User user3 = new User(3L, "user2", "sub-3", User.UserRole.USER);

        when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2, user3));

        // Act & Assert
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].role").value("USER"))
                .andExpect(jsonPath("$[1].role").value("ADMIN"))
                .andExpect(jsonPath("$[2].role").value("USER"));
    }

    // ==================== Username and CognitoSub Tests ====================

    @Test
    @WithMockUser(roles = "admin")
    void getUserById_ShouldReturnUserWithCognitoSub() throws Exception {
        // Arrange
        doNothing().when(authorizationService).requireAdmin(any());
        User userWithCognito = new User(1L, "testuser", "cognito-sub-xyz", User.UserRole.USER);
        when(userService.getUserById(1L)).thenReturn(Optional.of(userWithCognito));

        // Act & Assert
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cognitoSub").value("cognito-sub-xyz"));
    }

    @Test
    @WithMockUser(roles = "admin")
    void getUserById_ShouldReturnUserWithUniqueUsername() throws Exception {
        // Arrange
        doNothing().when(authorizationService).requireAdmin(any());
        when(userService.getUserById(1L)).thenReturn(Optional.of(regularUser));

        // Act & Assert - Username should be unique identifier
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.username").isString());
    }

}