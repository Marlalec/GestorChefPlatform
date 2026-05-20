package com.gestor.chef.gf.infrastructure.adapter.in.web.mapper;

import com.gestor.chef.gf.domain.model.User;
import com.gestor.chef.gf.infrastructure.adapter.in.web.dto.AuthResponse;
import com.gestor.chef.gf.infrastructure.adapter.in.web.dto.RegisterRequest;
import com.gestor.chef.gf.infrastructure.adapter.in.web.dto.UserResponse;

public final class UserWebMapper {

    private UserWebMapper() {
    }

    public static User toUser(RegisterRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .rol(request.getRol())
                .phone(request.getPhone())
                .build();
    }

    public static UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .rol(user.getRol())
                .phone(user.getPhone())
                .accountStatus(user.getAccountStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public static AuthResponse toAuthResponse(String token, User user) {
        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .rol(user.getRol())
                .phone(user.getPhone())
                .accountStatus(user.getAccountStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
