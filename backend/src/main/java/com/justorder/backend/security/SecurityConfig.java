package com.justorder.backend.security;

import java.util.Arrays;

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

    private final JwtAuthenticationFilter jwtAuthFilter;

    /**
     * Constructor injection (Best practice compared to @Autowired field injection).
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * Configures the main security filter chain for the application.
     * Disables CSRF, enforces stateless sessions, and defines access control rules 
     * for different API endpoints.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults()) 
            .csrf(csrf -> csrf.disable()) 
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public preflight checks and auth endpoints
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/sessions/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/hello").permitAll()
                
                // 1. PUBLIC REGISTRATION (Anyone can register)
                .requestMatchers(HttpMethod.POST, "/api/restaurants/create").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/customers/create").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/riders/create").permitAll()
                
                // 2. PUBLIC VIEWING AND CHECKOUT (Specific operations allowed for public/customers)
                .requestMatchers(HttpMethod.GET, "/api/restaurants/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/dishes/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/allergens/**", "/api/categories/**").permitAll() // Categorías y Alérgenos en modo lectura
                .requestMatchers(HttpMethod.POST, "/api/orders/checkout").permitAll()
                
                // 3. RESTAURANT OPERATIONS
                .requestMatchers(HttpMethod.PUT, "/api/restaurants/**").hasAnyRole("RESTAURANT", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/restaurants/**").hasAnyRole("RESTAURANT", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/restaurants/**").hasRole("ADMIN")
                
                // 4. CUSTOMER OPERATIONS
                .requestMatchers(HttpMethod.GET, "/api/customers/**").hasAnyRole("CUSTOMER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/customers/**").hasAnyRole("CUSTOMER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/customers/**").hasAnyRole("CUSTOMER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/customers/**").hasRole("ADMIN")
                
                // 5. RIDER OPERATIONS
                .requestMatchers(HttpMethod.GET, "/api/riders/**").hasAnyRole("RIDER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/riders/**").hasAnyRole("RIDER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/riders/**").hasAnyRole("RIDER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/riders/**").hasRole("ADMIN")
                
                // 6. DISH OPERATIONS
                .requestMatchers(HttpMethod.POST, "/api/dishes/**").hasAnyRole("RESTAURANT", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/dishes/**").hasAnyRole("RESTAURANT", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/dishes/**").hasAnyRole("RESTAURANT", "ADMIN")
                
                // 7. ORDER OPERATIONS (Admin general CRUD)
                .requestMatchers("/api/orders/**").hasRole("ADMIN")
                
                // 8. SYSTEM DICTIONARIES (Admin exclusive modifications)
                .requestMatchers("/api/allergens/**", "/api/categories/**", "/api/order-statuses/**").hasRole("ADMIN")
                
                // 9. ADMIN EXCLUSIVE OPERATIONS
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // Any other route not specifically mapped above requires generic authentication
                .anyRequest().authenticated()
            )
            // Custom JWT filter before the standard authentication filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configures the BCrypt password encoder bean.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the global CORS (Cross-Origin Resource Sharing) policy.
     */
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