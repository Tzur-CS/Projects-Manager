//package com.example.demo.config;
//
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.web.SecurityFilterChain;
//
///**
// * Security configuration for local development without Cognito
// * This allows testing all API endpoints via Swagger without authentication
// *
// * To use this profile, run: mvn spring-boot:run -Dspring-boot.run.profiles=local
// */
//@Configuration
//@EnableWebSecurity
//@Profile("local")
//@EnableAutoConfiguration(exclude = {OAuth2ResourceServerAutoConfiguration.class})
//public class LocalSecurityConfig {
//
//    @Bean
//    public SecurityFilterChain localFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(authz -> authz
//                        .anyRequest().permitAll() // Allow all requests without authentication
//                );
//        return http.build();
//    }
//}
//
