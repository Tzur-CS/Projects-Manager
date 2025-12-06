package com.example.demo.security;

import com.example.demo.exception.UnauthorizedAccessException;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

/**
 * Service for handling user authorization and role verification
 */
@Service
@Slf4j
public class AuthorizationService {

    private final UserRepository userRepository;

    @Autowired
    public AuthorizationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get the user ID from the authentication token
     */
    public String getUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new UnauthorizedAccessException("No authentication found");
        }

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            // Extract username or sub from JWT
            String userId = jwt.getClaimAsString("username");
            if (userId == null) {
                userId = jwt.getSubject();
            }
            if (userId == null) {
                throw new UnauthorizedAccessException("User ID not found in token");
            }
            return userId;
        }

        throw new UnauthorizedAccessException("Invalid authentication type");
    }

    /**
     * Verify that the authenticated user has admin role in the database
     * @throws UnauthorizedAccessException if user is not an admin
     */
    public void requireAdmin(Authentication authentication) {
        String userId = getUserId(authentication);
        log.debug("Checking admin access for user: {}", userId);

        User user = userRepository.findByUsername(userId)
                .or(() -> userRepository.findByCognitoSub(userId))
                .orElseThrow(() -> {
                    log.warn("User not found in database: {}", userId);
                    return new UnauthorizedAccessException("User not found in the system");
                });

        if (user.getRole() != User.UserRole.ADMIN) {
            log.warn("User {} attempted to access admin endpoint but has role: {}", userId, user.getRole());
            throw new UnauthorizedAccessException("Admin access required. You do not have administrative privileges.");
        }

        log.debug("Admin access granted for user: {}", userId);
    }

    /**
     * Check if the authenticated user is an admin (returns boolean instead of throwing)
     */
    public boolean isAdmin(Authentication authentication) {
        try {
            String userId = getUserId(authentication);
            User user = userRepository.findByUsername(userId)
                    .or(() -> userRepository.findByCognitoSub(userId))
                    .orElse(null);

            return user != null && user.getRole() == User.UserRole.ADMIN;
        } catch (Exception e) {
            log.warn("Error checking admin status: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Verify that the user exists in the database and return the user
     */
    public User getAuthenticatedUser(Authentication authentication) {
        String userId = getUserId(authentication);

        return userRepository.findByUsername(userId)
                .or(() -> userRepository.findByCognitoSub(userId))
                .orElseThrow(() -> {
                    log.warn("Authenticated user not found in database: {}", userId);
                    return new UnauthorizedAccessException("User not found in the system. Please contact administrator.");
                });
    }
}

