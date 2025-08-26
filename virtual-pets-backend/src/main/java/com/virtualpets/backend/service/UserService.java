package com.virtualpets.backend.service;

import com.virtualpets.backend.dto.response.UserResponse;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers(Collection<? extends GrantedAuthority> roles);
    UserResponse getUserByUsername(String requestedUsername, String callerUsername, Collection<? extends GrantedAuthority> roles);
}
