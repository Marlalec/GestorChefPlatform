package com.gestor.chef.gf.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private String id;
    private String tableNumber;
    private String status;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private String userId;
    private String channel;
    private String notes;
    private Instant createdAt;
    private Instant updatedAt;
}
