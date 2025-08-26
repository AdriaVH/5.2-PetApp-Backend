package com.virtualpets.backend.controller;

import com.virtualpets.backend.dto.response.UserResponse;
import com.virtualpets.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@SecurityRequirement(name = "bearerAuth")  // <-- add this
@RequiredArgsConstructor
@Tag(name = "Users", description = "Admin-only endpoints for managing users")
public class UserController {

    private final UserService userService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Fetch all registered users (Admin only)")
    public List<UserResponse> getAllUsers(Authentication auth) {
        Collection<? extends GrantedAuthority> roles = auth.getAuthorities();
        return userService.getAllUsers(roles);
    }
}
