package com.gestor.chef.gf.infrastructure.adapter.out.persistence;

import com.gestor.chef.gf.domain.model.AlertNotification;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document(collection = "alerts_notifications")
public class AlertNotificationDocument {

    @Id private String id;
    private String type;
    private String title;
    private String message;
    private String productId;
    private String orderId;
    @Indexed private String status;
    @Indexed private String targetUserId;
    @Indexed private Instant createdAt;
    private Instant readAt;

    public static AlertNotificationDocument fromDomain(AlertNotification a) {
        return AlertNotificationDocument.builder()
                .id(a.getId()).type(a.getType()).title(a.getTitle()).message(a.getMessage())
                .productId(a.getProductId()).orderId(a.getOrderId()).status(a.getStatus())
                .targetUserId(a.getTargetUserId()).createdAt(a.getCreatedAt()).readAt(a.getReadAt())
                .build();
    }

    public AlertNotification toDomain() {
        return AlertNotification.builder()
                .id(id).type(type).title(title).message(message)
                .productId(productId).orderId(orderId).status(status)
                .targetUserId(targetUserId).createdAt(createdAt).readAt(readAt)
                .build();
    }
}
