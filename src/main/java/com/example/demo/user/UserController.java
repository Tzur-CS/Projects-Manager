package com.example.demo.user;

import com.example.demo.security.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import com.example.demo.security.AuthorizationService;



import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management APIs")
@SecurityRequirement(name = "bearer-jwt")
@Slf4j
public class UserController {

    private final UserService userService;
    private final AuthorizationService authorizationService;

    @Autowired
    public UserController(UserService userService, AuthorizationService authorizationService) {
        this.userService = userService;
        this.authorizationService = authorizationService;
    }

    // ==================== Admin-Only Endpoints ====================

    @GetMapping
    @Operation(summary = "Get all users (Admin only)", description = "Retrieve all users in the system")
    public ResponseEntity<List<User>> getAllUsers(Authentication authentication) {
        authorizationService.requireAdmin(authentication);

        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID (Admin only)", description = "Retrieve a specific user by their ID")
    public ResponseEntity<User> getUserById(@PathVariable Long id, Authentication authentication) {
        authorizationService.requireAdmin(authentication);

        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/me")
    @Operation(summary = "Get my profile", description = "Retrieve the authenticated user's profile")
    public ResponseEntity<User> getMyProfile(Authentication authentication) {
        // For local testing without authentication, return a test user or first user
        if (authentication == null) {
            log.info("Getting profile (no authentication - local mode)");
            // Return the first user or create a test response
            return ResponseEntity.ok(new User()); // Or fetch a test user
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();
        String username = jwt.getClaim("username");

        if (username == null) {
            username = jwt.getSubject();
        }

        if (username != null) {
            return userService.getUserByUsername(username)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Extract user ID from JWT token
     */
    private String getUserId(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return jwt.getSubject();
    }
}

