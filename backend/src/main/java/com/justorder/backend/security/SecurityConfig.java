package com.justorder.backend.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

/**
 * Main security configuration class for the application.
 * Manages global security rules, including Cross-Origin Resource Sharing (CORS),
 * Cross-Site Request Forgery (CSRF) protection, stateless session management for JWT,
 * and endpoint authorization policies.
 * @version 1.0
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
     * @param http the {@link HttpSecurity} object to be configured.
     * @return the built {@link SecurityFilterChain} defining the security structure.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults()) 
            .csrf(csrf -> csrf.disable()) 
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public routes and preflight checks
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/hello").permitAll()
                
                // Public registration and viewing routes
                .requestMatchers("/api/restaurants/**").permitAll()
                .requestMatchers("/api/customers/**").permitAll()
                .requestMatchers("/api/riders/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/orders/checkout").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/allergens").permitAll() 
                .requestMatchers(HttpMethod.GET, "/api/dishes/**").permitAll()
                
                // Management routes (Requires specific roles)
                .requestMatchers(HttpMethod.POST, "/api/dishes/**").hasAnyRole("RESTAURANT", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/dishes/**").hasAnyRole("RESTAURANT", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/dishes/**").hasAnyRole("RESTAURANT", "ADMIN")
                
                // Protected admin-only routes
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // Any other route requires generic authentication
                .anyRequest().authenticated()
            )
            // Custom JWT filter before the standard authentication filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configures the BCrypt password encoder bean.
     * Used for securely hashing passwords and verifying credentials.
     * @return a {@link PasswordEncoder} instance utilizing BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the global CORS (Cross-Origin Resource Sharing) policy.
     * Allows the frontend (localhost:3000) to communicate safely with the backend.
     * @return a {@link CorsFilter} customized with allowed origins, headers, and methods.
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