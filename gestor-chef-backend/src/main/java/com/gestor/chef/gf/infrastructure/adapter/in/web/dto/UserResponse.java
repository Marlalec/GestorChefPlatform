package com.gestor.chef.gf.infrastructure.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String name;
    private String email;
    private String rol;
    private String phone;
    private String accountStatus;
    private Instant createdAt;
    private Instant updatedAt;
}
