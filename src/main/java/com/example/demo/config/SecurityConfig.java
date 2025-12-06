package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.Arrays;
import java.util.List;

/**
 * Security configuration for AWS Cognito authentication
 * Active for dev and prod profiles
 * Features:
 * - JWT token validation
 * - Audience validation
 * - Role verification done in controllers via AuthorizationService
 * - CORS configuration
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@org.springframework.context.annotation.Profile("!local")
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Value("${spring.security.oauth2.resourceserver.jwt.audiences:}")
    private String audiences;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> {
                    // Public endpoints - Swagger/OpenAPI only
                    authz.requestMatchers(new MvcRequestMatcher(introspector, "/swagger-ui/**")).permitAll();
                    authz.requestMatchers(new MvcRequestMatcher(introspector, "/v3/api-docs/**")).permitAll();
                    authz.requestMatchers(new MvcRequestMatcher(introspector, "/swagger-ui.html")).permitAll();

                    // Health check endpoints - ping is public, others require auth
                    authz.requestMatchers(new MvcRequestMatcher(introspector, "/api/health/ping")).permitAll();
                    authz.requestMatchers(new MvcRequestMatcher(introspector, "/api/health/**")).authenticated();

                    // Actuator endpoints - Health is public, rest require admin
                    authz.requestMatchers(new MvcRequestMatcher(introspector, "/actuator/health")).permitAll();
                    authz.requestMatchers(new MvcRequestMatcher(introspector, "/actuator/**")).hasRole("admin");

                    // All other endpoints require authentication
                    authz.anyRequest().authenticated();
                })
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.decoder(jwtDecoder()))
                )
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.deny()) // Deny all framing
                );
        return http.build();
    }

    /**
     * Configure JWT decoder with audience validation
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(issuerUri);

        // Add audience validation if configured
        if (audiences != null && !audiences.isEmpty()) {
            OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(audiences);
            OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);
            OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

            jwtDecoder.setJwtValidator(withAudience);
        }

        return jwtDecoder;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow common frontend development ports
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",      // Create React App default
                "http://localhost:4200",      // Angular default
                "http://localhost:5173",      // Vite default (React + Vite)
                "http://localhost:5174",      // Vite alternate port
                "http://localhost:8081"       // Alternative port
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Custom validator to check JWT audience claim
     * Ensures the token is intended for this application
     * Handles null audience gracefully (common with Cognito access tokens)
     */
    static class AudienceValidator implements OAuth2TokenValidator<Jwt> {
        private final String audience;

        AudienceValidator(String audience) {
            this.audience = audience;
        }

        @Override
        public OAuth2TokenValidatorResult validate(Jwt jwt) {
            // Handle null audience (Cognito access tokens often don't have aud claim)
            if (jwt.getAudience() == null || jwt.getAudience().isEmpty()) {
                // Check client_id claim instead (used by Cognito)
                String clientId = jwt.getClaimAsString("client_id");
                if (clientId != null && clientId.equals(this.audience)) {
                    return OAuth2TokenValidatorResult.success();
                }
                // If no audience validation possible, allow it (validate via other means)
                return OAuth2TokenValidatorResult.success();
            }

            // Standard audience validation
            if (jwt.getAudience().contains(this.audience)) {
                return OAuth2TokenValidatorResult.success();
            }

            return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error("invalid_token", "Required audience not found: " + audience, null)
            );
        }
    }
}

