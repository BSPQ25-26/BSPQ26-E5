package com.justorder.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

/**
 * Utility component for handling JSON Web Tokens (JWT).
 * Provides methods to generate securely signed tokens, extract claims (like email and roles), 
 * and validate the integrity and expiration of incoming tokens.
 * * @version 1.0
 */
@Component
public class JwtUtil {

    // Generates a secure cryptographic secret key for signing the tokens using HMAC-SHA algorithms
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    
    // Token expiration time in milliseconds (e.g., 10 hours)
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; 

    /**
     * Generates a new JWT for an authenticated user.
     * * @param email the user's email, which acts as the token's subject.
     * @param role the user's assigned role (e.g., "ROLE_ADMIN").
     * @return a signed JWT string containing the user's claims and expiration details.
     */
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role) // Stores the custom role claim inside the token payload
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    /**
     * Extracts the user's email (the 'subject' claim) from the provided token.
     * * @param token the JWT string.
     * @return the email stored within the token.
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the custom role claim from the provided token.
     * * @param token the JWT string.
     * @return the role stored within the token (e.g., "ROLE_ADMIN").
     */
    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }

    /**
     * Validates the provided token against a specific user's email.
     * Checks both that the token's subject matches the given email and that the token has not expired.
     * * @param token the JWT string to validate.
     * @param userEmail the email of the user attempting to authenticate.
     * @return true if the token is valid and belongs to the user, false otherwise.
     */
    public boolean isTokenValid(String token, String userEmail) {
        final String email = extractEmail(token);
        return (email.equals(userEmail) && !isTokenExpired(token));
    }

    /**
     * Checks whether the provided token has passed its expiration date.
     * * @param token the JWT string.
     * @return true if the token is expired, false otherwise.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date claim from the provided token.
     * * @param token the JWT string.
     * @return the exact Date when the token expires.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Generic helper method to extract a specific claim from the token using a resolver function.
     * * @param token the JWT string.
     * @param claimsResolver a function defining which claim to extract.
     * @param <T> the expected return type of the claim.
     * @return the extracted claim.
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parses the provided token using the cryptographic secret key to retrieve all its claims.
     * * @param token the JWT string.
     * @return the parsed Claims object containing the token's payload.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
    }
}