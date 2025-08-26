package com.virtualpets.backend.mapper;

import com.virtualpets.backend.dto.response.UserResponse;
import com.virtualpets.backend.model.User;

import java.util.stream.Collectors;

public class UserMapper {

    public static UserResponse toResponse(User user) {
        // Convert Role entities to role names
        var roleNames = user.getRoles()
                .stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet());

        return new UserResponse(user.getId(), user.getUsername(), roleNames);
    }
}
