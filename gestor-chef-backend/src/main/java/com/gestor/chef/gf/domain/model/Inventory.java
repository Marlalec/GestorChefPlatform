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
public class Inventory {
    private String id;
    private String productId;
    private String productName;
    private String category;
    private double currentQuantity;
    private double minimumQuantity;
    private String unit;
    private boolean lowStockAlert;
    private Instant lastUpdated;
}
