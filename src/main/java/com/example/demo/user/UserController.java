package com.example.demo.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management APIs")
@SecurityRequirement(name = "bearer-jwt")
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ==================== Admin-Only Endpoints ====================

    @GetMapping
    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "Get all users (Admin only)", description = "Retrieve all users in the system")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "Get user by ID (Admin only)", description = "Retrieve a specific user by their ID")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "Get user by email (Admin only)", description = "Retrieve a user by their email address")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "Create user (Admin only)", description = "Create a new user (admin only)")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "Update user (Admin only)", description = "Update any user's information (admin only)")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "Delete user (Admin only)", description = "Delete any user (admin only)")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== Self-Service Endpoints (Any Authenticated User) ====================

    @PostMapping("/signup")
    @Operation(
        summary = "User signup",
        description = "Create user profile after Cognito authentication. Users are created in AWS Cognito first, then this creates their profile in the database."
    )
    public ResponseEntity<User> signup(@Valid @RequestBody User user, Authentication authentication) {
        try {
            // For local testing without authentication
            if (authentication == null) {
                // Check if user already exists by email
                if (user.getEmail() != null && userService.getUserByEmail(user.getEmail()).isPresent()) {
                    return ResponseEntity.badRequest().build();
                }
                User createdUser = userService.createUser(user);
                return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
            }

            // Optionally verify the user doesn't already exist
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String email = jwt.getClaim("email");

            // Check if user already exists
            if (email != null && userService.getUserByEmail(email).isPresent()) {
                return ResponseEntity.badRequest().build();
            }

            // Create user profile linked to Cognito user
            User createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
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
        String email = jwt.getClaim("email");

        if (email != null) {
            return userService.getUserByEmail(email)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/me")
    @Operation(summary = "Update my profile", description = "Update the authenticated user's own profile")
    public ResponseEntity<User> updateMyProfile(@Valid @RequestBody User user, Authentication authentication) {
        try {
            // For local testing without authentication
            if (authentication == null) {
                log.info("Updating profile (no authentication - local mode)");
                // Update by user ID if provided, otherwise return bad request
                if (user.getId() != null) {
                    User updatedUser = userService.updateUser(user.getId(), user);
                    return ResponseEntity.ok(updatedUser);
                }
                return ResponseEntity.badRequest().build();
            }

            Jwt jwt = (Jwt) authentication.getPrincipal();
            String email = jwt.getClaim("email");

            if (email != null) {
                // Find user by email and update
                return userService.getUserByEmail(email)
                        .map(existingUser -> {
                            User updatedUser = userService.updateUser(existingUser.getId(), user);
                            return ResponseEntity.ok(updatedUser);
                        })
                        .orElse(ResponseEntity.notFound().build());
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/me")
    @Operation(summary = "Delete my account", description = "Delete the authenticated user's own account")
    public ResponseEntity<Void> deleteMyAccount(Authentication authentication) {
        try {
            // For local testing without authentication
            if (authentication == null) {
                log.info("Deleting account (no authentication - local mode)");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Jwt jwt = (Jwt) authentication.getPrincipal();
            String email = jwt.getClaim("email");

            if (email != null) {
                return userService.getUserByEmail(email)
                        .map(existingUser -> {
                            userService.deleteUser(existingUser.getId());
                            return ResponseEntity.noContent().<Void>build();
                        })
                        .orElse(ResponseEntity.notFound().build());
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Extract user ID from JWT token
     */
    private String getUserId(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return jwt.getSubject();
    }
}

