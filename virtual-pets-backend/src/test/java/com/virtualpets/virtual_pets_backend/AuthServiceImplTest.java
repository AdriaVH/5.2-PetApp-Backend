package com.virtualpets.virtual_pets_backend;

import com.virtualpets.backend.dto.request.LoginRequest;
import com.virtualpets.backend.dto.request.RegisterRequest;
import com.virtualpets.backend.dto.response.AuthResponse;
import com.virtualpets.backend.model.Role;
import com.virtualpets.backend.model.User;
import com.virtualpets.backend.repository.RoleRepository;
import com.virtualpets.backend.repository.UserRepository;
import com.virtualpets.backend.service.impl.AuthServiceImpl;
import com.virtualpets.backend.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    // Use real JwtUtil instead of mocking
    private final JwtUtil jwtUtil = new JwtUtil();

    private Role role;

    @BeforeEach
    void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Manually inject real JwtUtil
        authService = new AuthServiceImpl(userRepository, roleRepository, passwordEncoder, jwtUtil);

        role = new Role(1L, "ROLE_USER");
    }

    @Test
    void register_success() {
        RegisterRequest request = new RegisterRequest("alice", "pass");

        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");

        AuthResponse response = authService.register(request);

        assertNotNull(response.token()); // real token
        assertEquals("alice", response.username());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void login_success() {
        LoginRequest request = new LoginRequest("alice", "pass");
        User user = User.builder()
                .username("alice")
                .password("encodedPass")
                .roles(Set.of(role))
                .build();

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass", "encodedPass")).thenReturn(true);

        AuthResponse response = authService.login(request);

        assertNotNull(response.token()); // real token
        assertEquals("alice", response.username());
        verify(userRepository, never()).save(any(User.class)); // login should not save
    }

    @Test
    void login_invalidPassword_throws() {
        LoginRequest request = new LoginRequest("alice", "wrong");
        User user = User.builder()
                .username("alice")
                .password("encodedPass")
                .roles(Set.of(role))
                .build();

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encodedPass")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(request));
        assertEquals("Invalid credentials", ex.getMessage());
    }

    @Test
    void login_userNotFound_throws() {
        LoginRequest request = new LoginRequest("bob", "pass");

        when(userRepository.findByUsername("bob")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(request));
        assertEquals("User not found", ex.getMessage());
    }
}
