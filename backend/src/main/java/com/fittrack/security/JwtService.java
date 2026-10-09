package com.fittrack.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service responsible for generating, signing, parsing, and validating JSON Web Tokens (JWT).
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    /**
     * Derives HMAC-SHA secret signing key from the configured secret string.
     *
     * @return cryptographically secure HMAC-SHA key
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extracts username (subject email) from the JWT claims.
     *
     * @param token JWT token string
     * @return username subject
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Resolves a specific claim using the supplied resolver function.
     *
     * @param token          JWT token string
     * @param claimsResolver function to extract specific claim
     * @param <T>            claim value type
     * @return extracted claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generates a JWT token for the specified user details without additional custom claims.
     *
     * @param userDetails Spring Security user details
     * @return signed JWT string
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates a signed JWT token containing custom claims for the given user details.
     *
     * @param extraClaims map of additional custom claims to embed
     * @param userDetails Spring Security user details
     * @return signed JWT string
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails.getUsername(), jwtExpiration);
    }

    /**
     * Generates a signed JWT token containing custom claims for a username string.
     *
     * @param extraClaims map of additional custom claims to embed
     * @param username    username subject
     * @return signed JWT string
     */
    public String generateToken(Map<String, Object> extraClaims, String username) {
        return buildToken(extraClaims, username, jwtExpiration);
    }

    /**
     * Constructs and cryptographically signs a JWT token using HS256.
     *
     * @param extraClaims map of extra claims
     * @param subject     subject identifier (email)
     * @param expiration  time-to-live in milliseconds
     * @return signed JWT string
     */
    private String buildToken(Map<String, Object> extraClaims, String subject, long expiration) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Verifies whether a JWT token is valid for the specified user and is not expired.
     *
     * @param token       JWT token string
     * @param userDetails Spring Security user details
     * @return true if token is valid and active, false otherwise
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Checks if a token has passed its expiration time.
     *
     * @param token JWT token string
     * @return true if token is expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts expiration timestamp from token claims.
     *
     * @param token JWT token string
     * @return Date of expiration
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Parses and cryptographically validates all claims in the JWT token.
     *
     * @param token JWT token string
     * @return Claims payload
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
