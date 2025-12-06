package com.example.demo.health;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Health check endpoint to verify frontend-backend connectivity and Cognito authentication
 */
@RestController
@RequestMapping("/api/health")
@Tag(name = "Health", description = "Health check and connectivity verification")
@Slf4j
public class HealthController {

    /**
     * Public health check endpoint - no authentication required
     * Tests basic connectivity
     */
    @GetMapping("/ping")
    @Operation(summary = "Ping endpoint", description = "Basic connectivity test - no authentication required")
    public ResponseEntity<Map<String, Object>> ping() {
        log.info("Health ping received");

        Map<String, Object> response = new HashMap<>();
        response.put("status", "ok");
        response.put("message", "Backend is reachable");
        response.put("timestamp", Instant.now().toString());
        response.put("service", "Task Management System");

        return ResponseEntity.ok(response);
    }

    /**
     * Authenticated health check endpoint
     * Tests Cognito authentication and returns user information
     */
    @GetMapping("/auth")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Authenticated health check", description = "Verify Cognito authentication and get user info")
    public ResponseEntity<Map<String, Object>> authHealth(Authentication authentication) {
        log.info("Authenticated health check received");

        Map<String, Object> response = new HashMap<>();

        // Check if authenticated
        if (authentication == null) {
            response.put("authenticated", false);
            response.put("message", "No authentication found - running in local mode");
            response.put("timestamp", Instant.now().toString());
            return ResponseEntity.ok(response);
        }

        // Extract JWT information
        Jwt jwt = (Jwt) authentication.getPrincipal();

        // Build response with user information
        response.put("authenticated", true);
        response.put("status", "ok");
        response.put("message", "Successfully authenticated with Cognito");
        response.put("timestamp", Instant.now().toString());

        // User information
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", jwt.getSubject());
        userInfo.put("username", jwt.getClaimAsString("cognito:username"));
        userInfo.put("email", jwt.getClaimAsString("email"));

        // Roles/Groups
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        userInfo.put("roles", roles);
        userInfo.put("groups", jwt.getClaimAsStringList("cognito:groups"));

        response.put("user", userInfo);

        // Token information
        Map<String, Object> tokenInfo = new HashMap<>();
        tokenInfo.put("issuer", jwt.getIssuer().toString());
        tokenInfo.put("audience", jwt.getAudience());
        tokenInfo.put("issuedAt", jwt.getIssuedAt().toString());
        tokenInfo.put("expiresAt", jwt.getExpiresAt().toString());
        tokenInfo.put("tokenUse", jwt.getClaimAsString("token_use"));

        response.put("token", tokenInfo);

        // Authentication details
        response.put("authenticationName", authentication.getName());
        response.put("authenticationClass", authentication.getClass().getSimpleName());

        log.info("Health check successful for user: {}", jwt.getSubject());

        return ResponseEntity.ok(response);
    }


    /**
     * User info endpoint - returns current user's Cognito information
     */
    @GetMapping("/me")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Get current user info", description = "Get authenticated user's information from Cognito")
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("authenticated", false);
            response.put("message", "No authentication - running in local mode");
            return ResponseEntity.ok(response);
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("authenticated", true);
        userInfo.put("userId", jwt.getSubject());
        userInfo.put("username", jwt.getClaimAsString("cognito:username"));
        userInfo.put("email", jwt.getClaimAsString("email"));
        userInfo.put("emailVerified", jwt.getClaimAsBoolean("email_verified"));
        userInfo.put("roles", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        userInfo.put("groups", jwt.getClaimAsStringList("cognito:groups"));

        // Add all available claims for debugging
        Map<String, Object> allClaims = new HashMap<>(jwt.getClaims());
        userInfo.put("claims", allClaims);

        log.info("User info retrieved for: {}", jwt.getSubject());

        return ResponseEntity.ok(userInfo);
    }

    /**
     * Helper method to extract user ID from authentication
     */
    private String getUserId(Authentication authentication) {
        if (authentication == null) {
            return "anonymous";
        }
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return jwt.getSubject();
    }

    /**
     * Get active Spring profile
     */
    private String getActiveProfile() {
        String profile = System.getProperty("spring.profiles.active");
        return profile != null ? profile : "default";
    }

    /**
     * Get system uptime (simplified)
     */
    private String getUptime() {
        long uptime = System.currentTimeMillis() / 1000; // seconds since epoch
        return uptime + " seconds";
    }
}

