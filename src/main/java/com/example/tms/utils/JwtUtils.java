package com.example.tms.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JWT util class
 */
@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.lifetime}")
    private Duration jwtLifetime;

    @Value("${rt.lifetime}")
    private Duration rtLifetime;

    /**
     * Generates token based on users authorities
     * @param userDetails Details of user
     * @return Bearer token
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        List<String> rolesList = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        claims.put("roles", rolesList);

        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + jwtLifetime.toMillis());
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(issuedDate)
                .expiration(expiredDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     *
     * @param token Requested bearer token
     * @param userDetails User details
     * @return true: if token is valid, false: token is not valid
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Check token expiration
     * @param token Requested token
     * @return true: token is expired, false: token is not expired
     */
    private boolean isTokenExpired(String token) {
        return getAllClaimsFromToken(token).getExpiration().before(new Date());
    }

    /**
     *
     * @param extraClaims
     * @param userDetails
     * @return refresh token
     */
    public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + rtLifetime.toMillis());
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(issuedDate)
                .expiration(expiredDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     *
     * @param token Requested token
     * @return all claims of token (duration, user authorities)
     */
    private Claims getAllClaimsFromToken(String token) {
        SecretKey secretKey = new SecretKeySpec(this.secret.getBytes(StandardCharsets.UTF_8),
                io.jsonwebtoken.SignatureAlgorithm.HS256.getJcaName());
        return Jwts.parser()
                .verifyWith(secretKey)
                .build().parseSignedClaims(token).getPayload();
    }

    /**
     *
     * @param token Requested token
     * @return username
     */
    public String getUsername(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    /**
     *
     * @param token Requested token
     * @return List of roles of user
     */
    public List<String> getRoles(String token) {
        return getAllClaimsFromToken(token).get("roles", List.class);
    }

    /**
     *
     * @return encoded secret key
     */
    private Key getSigningKey() {
        byte[] keyBytes = this.secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
