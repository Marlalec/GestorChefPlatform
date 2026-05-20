package com.gestor.chef.gf.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Waste {

    private String id;

    private String productId;
    private String productName;

    private double quantityWasted;
    private String unit;

    private String cause;

    private BigDecimal estimatedCost;

    private String description;

    private String reportedBy;
    private String reportedByName;

    private Instant occurredAt;

    private Instant registeredAt;

    private String inventoryMovementId;
}
