package com.gestor.chef.gf.infrastructure.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuthResponse {
    private String token;
    @Builder.Default
    private String type = "Bearer";
    private String userId;
    private String name;
    private String email;
    private String rol;
    private String phone;
    private String accountStatus;
    private Instant createdAt;
    private Instant updatedAt;
}
