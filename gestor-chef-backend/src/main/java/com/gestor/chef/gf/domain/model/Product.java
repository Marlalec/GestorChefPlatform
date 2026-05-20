package com.gestor.chef.gf.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private String id;
    private String name;
    private String category;
    private String supplierId;
    private String supplierName;
    private double quantity;
    private double minimumQuantity;
    private String unit;
    private BigDecimal price;
    private LocalDate expirationDate;
    private String status;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;

    public boolean isLowStock() {
        return quantity < minimumQuantity;
    }

    public boolean isExpiringBefore(LocalDate date) {
        return expirationDate != null && expirationDate.isBefore(date);
    }
}
