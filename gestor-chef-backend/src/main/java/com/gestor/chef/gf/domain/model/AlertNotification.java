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
public class AlertNotification {
    private String id;
    private String type;
    private String title;
    private String message;
    private String productId;
    private String orderId;
    private String status;
    private String targetUserId;
    private Instant createdAt;
    private Instant readAt;
}
