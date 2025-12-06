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
        testUser = new User(1L, "johndoe", "test-cognito-sub-123", User.UserRole.USER);
    }

    @Test
    @WithMockUser(roles = "admin")
    void getAllUsers_ShouldReturnUserList() throws Exception {
        // Arrange
        when(userService.getAllUsers()).thenReturn(Arrays.asList(testUser));

        // Act & Assert
        mockMvc.perform(get("/api/users").with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("johndoe"))
                .andExpect(jsonPath("$[0].cognitoSub").value("test-cognito-sub-123"))
                .andExpect(jsonPath("$[0].role").value("USER"));
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
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.cognitoSub").value("test-cognito-sub-123"))
                .andExpect(jsonPath("$.role").value("USER"));
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
    void getUserByUsername_ShouldReturnUser_WhenUsernameExists() throws Exception {
        // Arrange
        when(userService.getUserByUsername("johndoe")).thenReturn(Optional.of(testUser));

        // Act & Assert
        mockMvc.perform(get("/api/users/username/johndoe")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.cognitoSub").value("test-cognito-sub-123"));
    }

}