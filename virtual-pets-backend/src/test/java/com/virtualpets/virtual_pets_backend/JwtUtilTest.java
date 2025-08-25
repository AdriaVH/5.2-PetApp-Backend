package com.virtualpets.virtual_pets_backend;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.virtualpets.backend.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
    }

    @Test
    void generateToken_and_extractUsername() {
        String token = jwtUtil.generateToken("admin", Set.of("ROLE_ADMIN", "ROLE_USER"));

        String username = jwtUtil.getUsername(token);
        List<String> roles = jwtUtil.getRoles(token);

        assertEquals("admin", username);
        assertTrue(roles.contains("ROLE_ADMIN"));
        assertTrue(roles.contains("ROLE_USER"));
    }

    @Test
    void validateToken_returnsDecodedJWT() {
        String token = jwtUtil.generateToken("alice", Set.of("ROLE_USER"));

        var decodedJWT = jwtUtil.validateToken(token);

        assertEquals("alice", decodedJWT.getSubject());
        assertTrue(decodedJWT.getClaim("roles").asList(String.class).contains("ROLE_USER"));
    }

    @Test
    void invalidToken_throwsJWTVerificationException() {
        String invalidToken = "invalid.token.value";

        assertThrows(JWTVerificationException.class, () -> jwtUtil.validateToken(invalidToken));
    }

    @Test
    void getRoles_returnsCorrectRoles() {
        String token = jwtUtil.generateToken("bob", Set.of("ROLE_USER", "ROLE_ADMIN"));

        List<String> roles = jwtUtil.getRoles(token);

        assertEquals(2, roles.size());
        assertTrue(roles.contains("ROLE_USER"));
        assertTrue(roles.contains("ROLE_ADMIN"));
    }

}
