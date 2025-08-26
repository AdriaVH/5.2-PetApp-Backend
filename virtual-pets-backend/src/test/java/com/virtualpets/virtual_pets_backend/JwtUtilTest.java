package com.virtualpets.virtual_pets_backend;

import com.virtualpets.backend.exception.JwtAuthenticationException;
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
    void generateToken_shouldReturnTokenWithCorrectUsernameAndRoles() {
        String token = jwtUtil.generateToken("admin", Set.of("ROLE_ADMIN", "ROLE_USER"));

        String username = jwtUtil.getUsername(token);
        List<String> roles = jwtUtil.getRoles(token);

        assertEquals("admin", username, "Username extracted from token should match");
        assertTrue(roles.contains("ROLE_ADMIN"), "Roles should contain ROLE_ADMIN");
        assertTrue(roles.contains("ROLE_USER"), "Roles should contain ROLE_USER");
    }

    @Test
    void verifyToken_shouldReturnDecodedJWT() {
        String token = jwtUtil.generateToken("alice", Set.of("ROLE_USER"));

        var decodedJWT = jwtUtil.verifyToken(token);

        assertEquals("alice", decodedJWT.getSubject(), "Decoded JWT should have correct subject");
        assertTrue(decodedJWT.getClaim("roles").asList(String.class).contains("ROLE_USER"),
                "Decoded JWT should contain ROLE_USER in roles claim");
    }

    @Test
    void verifyToken_invalidToken_shouldThrowJwtAuthenticationException() {
        String invalidToken = "invalid.token.value";

        JwtAuthenticationException exception = assertThrows(JwtAuthenticationException.class, () ->
                jwtUtil.verifyToken(invalidToken)
        );

        assertEquals("Invalid or expired JWT token", exception.getMessage());
    }

    @Test
    void getRoles_shouldReturnCorrectRoles() {
        String token = jwtUtil.generateToken("bob", Set.of("ROLE_USER", "ROLE_ADMIN"));

        List<String> roles = jwtUtil.getRoles(token);

        assertEquals(2, roles.size(), "Roles list size should match");
        assertTrue(roles.contains("ROLE_USER"), "Roles should contain ROLE_USER");
        assertTrue(roles.contains("ROLE_ADMIN"), "Roles should contain ROLE_ADMIN");
    }

    @Test
    void getUsername_shouldReturnCorrectUsername() {
        String token = jwtUtil.generateToken("carol", Set.of("ROLE_USER"));

        String username = jwtUtil.getUsername(token);

        assertEquals("carol", username, "Username extracted should match the token owner");
    }
}
