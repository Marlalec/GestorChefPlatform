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
public class InventoryMovement {
    private String id;
    private String productId;
    private String productName;
    private double quantityChanged;
    private String movementType;
    private String reason;
    private String userId;
    private String userName;
    private Double weightFromScale;
    private String notes;
    private Instant timestamp;
}
