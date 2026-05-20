package com.gestor.chef.gf.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {
    private String id;
    private String name;
    private String contactName;
    private String email;
    private String phone;
    private String address;
    private List<String> productIds;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;
}
