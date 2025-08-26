package com.virtualpets.backend.service.impl;

import com.virtualpets.backend.dto.response.UserResponse;
import com.virtualpets.backend.exception.UnauthorizedActionException;
import com.virtualpets.backend.exception.ResourceNotFoundException;
import com.virtualpets.backend.mapper.UserMapper;
import com.virtualpets.backend.model.User;
import com.virtualpets.backend.repository.UserRepository;
import com.virtualpets.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Cacheable(value = "allUsers")
    public List<UserResponse> getAllUsers(Collection<? extends GrantedAuthority> roles) {
        if (!isAdmin(roles)) {
            throw new UnauthorizedActionException("Only admins can access all users");
        }

        return userRepository.findAll().stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "userByUsername", key = "#requestedUsername")
    public UserResponse getUserByUsername(String requestedUsername, String callerUsername, Collection<? extends GrantedAuthority> roles) {
        User user = userRepository.findByUsername(requestedUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!canAccessUser(requestedUsername, callerUsername, roles)) {
            throw new UnauthorizedActionException("You are not allowed to view this user");
        }

        return UserMapper.toResponse(user);
    }

    // Optional: invalidate cache if a user is updated/deleted
    @CacheEvict(value = {"allUsers", "userByUsername"}, allEntries = true)
    public void evictAllCaches() {
        // This can be called after user create/update/delete operations
    }

    // --- Helpers ---
    private boolean isAdmin(Collection<? extends GrantedAuthority> roles) {
        return roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
    }

    private boolean canAccessUser(String requestedUsername, String callerUsername, Collection<? extends GrantedAuthority> roles) {
        return requestedUsername.equals(callerUsername) || isAdmin(roles);
    }
}
