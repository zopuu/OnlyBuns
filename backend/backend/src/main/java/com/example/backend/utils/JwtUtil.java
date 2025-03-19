package com.example.backend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;


import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final String COOKIE_NAME = "jwt";

    public String generateTokenAndSetCookie(UserDetails userDetails) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", userDetails.getAuthorities())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationMs))
                .signWith(secretKey)
                .compact();

    }
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }
    public boolean isTokenExpired(String token) {
        Date expiration = parseClaims(token).getExpiration();
        return expiration.before(new Date());
    }
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token).getBody();
    }
}
