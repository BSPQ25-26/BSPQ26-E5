package com.justorder.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Custom security filter that intercepts every HTTP request to validate JWT tokens.
 * Ensures that protected endpoints are only accessed by users providing a valid, 
 * unexpired token with the appropriate roles.
 * * @version 1.0
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Performs the actual filtering logic for incoming requests.
     * Extracts the JWT from the Authorization header, validates its signature and expiration,
     * and populates the Spring SecurityContext if the token is valid.
     * * @param request the incoming HTTP request.
     * @param response the outgoing HTTP response.
     * @param filterChain the chain of filters to pass the request and response to.
     * @throws ServletException if a servlet-specific error occurs during filtering.
     * @throws IOException if an I/O error occurs during filtering.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 1. Retrieve the Authorization header
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // If there is no token or it does not start with "Bearer ", proceed with the filter chain (security will block it later if required)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extract the token (removing the "Bearer " prefix)
        jwt = authHeader.substring(7);

        // This application mixes JWT auth and session-token auth (UUID tokens).
        // Ignore non-JWT Bearer values so session-based flows can continue.
        if (!looksLikeJwt(jwt)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            userEmail = jwtUtil.extractEmail(jwt);
        } catch (JwtException | IllegalArgumentException ex) {
            // Invalid JWT should not crash request processing for permitAll/session endpoints.
            filterChain.doFilter(request, response);
            return;
        }

        // 3. If an email is present and the security context is not yet authenticated
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // Validate that the token is valid and has not expired
            if (jwtUtil.isTokenValid(jwt, userEmail)) {
                
                // Extract the role (e.g., ROLE_ADMIN)
                String role = jwtUtil.extractRole(jwt);
                
                // Create the authentication token for Spring containing the user's role
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userEmail, 
                        null, 
                        Collections.singletonList(new SimpleGrantedAuthority(role))
                );
                
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Inform Spring Security: "This user has successfully passed the authentication check"
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private boolean looksLikeJwt(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        int dotCount = 0;
        for (int i = 0; i < token.length(); i++) {
            if (token.charAt(i) == '.') {
                dotCount++;
            }
        }
        return dotCount == 2;
    }
}