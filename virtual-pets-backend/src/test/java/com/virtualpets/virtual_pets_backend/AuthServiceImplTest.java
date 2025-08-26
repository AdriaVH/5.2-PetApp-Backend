package com.virtualpets.virtual_pets_backend;

import com.virtualpets.backend.dto.request.LoginRequest;
import com.virtualpets.backend.dto.request.RegisterRequest;
import com.virtualpets.backend.dto.response.AuthResponse;
import com.virtualpets.backend.exception.InvalidCredentialsException;
import com.virtualpets.backend.exception.UserAlreadyExistsException;
import com.virtualpets.backend.model.Role;
import com.virtualpets.backend.model.User;
import com.virtualpets.backend.repository.RoleRepository;
import com.virtualpets.backend.repository.UserRepository;
import com.virtualpets.backend.service.impl.AuthServiceImpl;
import com.virtualpets.backend.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

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

    // Use real JwtUtil
    private final JwtUtil jwtUtil = new JwtUtil();

    private Role role;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthServiceImpl(userRepository, roleRepository, passwordEncoder, jwtUtil);
        role = new Role(1L, "ROLE_USER");
    }

    @Test
    void register_success() {
        RegisterRequest request = new RegisterRequest("alice", "pass");

        when(userRepository.findByUsername("alice")).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");

        AuthResponse response = authService.register(request);

        assertNotNull(response.token());
        assertEquals("alice", response.username());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_existingUsername_throws() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(new User()));
        RegisterRequest request = new RegisterRequest("alice", "pass");

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));
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

        assertNotNull(response.token());
        assertEquals("alice", response.username());
        verify(userRepository, never()).save(any(User.class));
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

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void login_userNotFound_throws() {
        LoginRequest request = new LoginRequest("bob", "pass");

        when(userRepository.findByUsername("bob")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }
}
