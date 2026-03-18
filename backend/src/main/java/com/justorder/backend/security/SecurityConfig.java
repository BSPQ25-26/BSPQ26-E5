package com.justorder.backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * Main security configuration class for the application.
 * Manages the global security rules, including Cross-Origin Resource Sharing (CORS),
 * Cross-Site Request Forgery (CSRF) protection, stateless session management for JWT,
 * and endpoint authorization policies.
 * * @version 1.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    /**
     * Configures the main security filter chain for the application.
     * Disables CSRF, enforces stateless sessions, and defines access control rules 
     * for different API endpoints.
     * * @param http the {@link HttpSecurity} object to be configured.
     * @return the built {@link SecurityFilterChain} defining the application's security structure.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults()) 
            .csrf(csrf -> csrf.disable()) 
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public routes (Login)
                .requestMatchers("/api/auth/**").permitAll() 
                // Protected routes (Only users with the ROLE_ADMIN role)
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // Any other route will require generic authentication
                .anyRequest().authenticated()
            )
            // Add our custom JWT "gatekeeper" filter just before the standard Spring authentication filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configures the BCrypt password encoder bean.
     * Used for securely hashing passwords before storing them in the database 
     * and for verifying credentials during login.
     * * @return a {@link PasswordEncoder} instance utilizing the BCrypt hashing algorithm.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the global CORS (Cross-Origin Resource Sharing) policy.
     * Allows the frontend application (e.g., running on localhost:3000) to communicate 
     * securely with this backend without running into browser security blocks.
     * * @return a {@link CorsFilter} customized with the allowed origins, headers, and HTTP methods.
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000")); 
        config.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH"));
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}