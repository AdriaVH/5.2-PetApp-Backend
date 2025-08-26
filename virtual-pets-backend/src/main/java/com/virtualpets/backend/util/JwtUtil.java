package com.virtualpets.backend.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.virtualpets.backend.exception.JwtAuthenticationException;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JwtUtil {

    private static final String SECRET = "your-secret-key";
    private static final long EXPIRATION_MS = 1000 * 60 * 60; // 1 hour

    private final Algorithm algorithm = Algorithm.HMAC256(SECRET);

    public String generateToken(String username, Set<String> roles) {
        return JWT.create()
                .withSubject(username)
                .withClaim("roles", new ArrayList<>(roles))
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .sign(algorithm);
    }

    public DecodedJWT verifyToken(String token) {
        try {
            return JWT.require(algorithm).build().verify(token);
        } catch (JWTVerificationException ex) {
            throw new JwtAuthenticationException("Invalid or expired JWT token");
        }
    }

    public String getUsername(String token) {
        return verifyToken(token).getSubject();
    }

    public List<String> getRoles(String token) {
        return verifyToken(token).getClaim("roles").asList(String.class);
    }
}
