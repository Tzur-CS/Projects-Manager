package com.example.demo.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for UserController
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User(1L, "John Doe", "john@example.com", "1234567890", "johndoe", "test-cognito-sub-123", User.UserRole.USER);
    }

    @Test
    @WithMockUser(roles = "admin")
    void getAllUsers_ShouldReturnUserList() throws Exception {
        // Arrange
        when(userService.getAllUsers()).thenReturn(Arrays.asList(testUser));

        // Act & Assert
        mockMvc.perform(get("/api/users").with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].email").value("john@example.com"))
                .andExpect(jsonPath("$[0].phone").value("1234567890"));
    }

    @Test
    @WithMockUser(roles = "admin")
    void getUserById_ShouldReturnUser_WhenUserExists() throws Exception {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

        // Act & Assert
        mockMvc.perform(get("/api/users/1").with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    @WithMockUser(roles = "admin")
    void getUserById_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/users/1").with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "admin")
    void getUserByEmail_ShouldReturnUser_WhenEmailExists() throws Exception {
        // Arrange
        when(userService.getUserByEmail("john@example.com")).thenReturn(Optional.of(testUser));

        // Act & Assert
        mockMvc.perform(get("/api/users/email/john@example.com")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    @WithMockUser(roles = "admin")
    void createUser_ShouldReturnCreatedUser() throws Exception {
        // Arrange
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    @WithMockUser(roles = "admin")
    void createUser_ShouldReturnBadRequest_WhenEmailExists() throws Exception {
        // Arrange
        when(userService.createUser(any(User.class)))
                .thenThrow(new IllegalArgumentException("Email already exists"));

        // Act & Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "admin")
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        // Arrange
        User updatedUser = new User(1L, "Jane Doe", "jane@example.com", "0987654321", "janedoe", "test-cognito-sub-456", User.UserRole.USER);
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

        // Act & Assert
        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.email").value("jane@example.com"))
                .andExpect(jsonPath("$.phone").value("0987654321"));
    }

    @Test
    @WithMockUser(roles = "admin")
    void updateUser_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        // Arrange
        User updatedUser = new User(1L, "Jane Doe", "jane@example.com", "0987654321", "janedoe", "test-cognito-sub-456", User.UserRole.USER);
        when(userService.updateUser(eq(1L), any(User.class)))
                .thenThrow(new IllegalArgumentException("User not found"));

        // Act & Assert
        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "admin")
    void deleteUser_ShouldReturnNoContent() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/users/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "admin")
    void deleteUser_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("User not found"))
                .when(userService).deleteUser(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/users/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound());
    }
}

