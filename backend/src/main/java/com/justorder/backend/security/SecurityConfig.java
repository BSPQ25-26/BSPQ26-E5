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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults()) 
            .csrf(csrf -> csrf.disable()) 
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/sessions/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/hello").permitAll()
                .requestMatchers("/api/restaurants/**").permitAll()
                .requestMatchers("/api/customers/**").permitAll()
                .requestMatchers("/api/riders/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/orders/checkout").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/allergens").permitAll() 
                .requestMatchers(HttpMethod.GET, "/api/dishes/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/sessions/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/dishes/**").permitAll()//.hasAnyRole("RESTAURANT", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/dishes/**").permitAll()//.hasAnyRole("RESTAURANT", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/dishes/**").permitAll()//.hasAnyRole("RESTAURANT", "ADMIN")
                // Rutas protegidas (Solo usuarios con el rol ROLE_ADMIN)
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            // Añadimos nuestro portero justo antes del filtro normal de Spring
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://127.0.0.1:3000",
            "http://localhost:3001",
            "http://127.0.0.1:3001",
            "http://localhost:5173",
            "http://127.0.0.1:5173"
        ));
        config.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH"));
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}