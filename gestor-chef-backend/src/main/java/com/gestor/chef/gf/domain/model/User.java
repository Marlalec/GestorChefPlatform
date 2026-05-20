package com.gestor.chef.gf.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id;
    private String name;
    private String email;
    private String password;
    private String rol;
    private String accountStatus;
    private String phone;
    private Instant createdAt;
    private Instant updatedAt;
}
