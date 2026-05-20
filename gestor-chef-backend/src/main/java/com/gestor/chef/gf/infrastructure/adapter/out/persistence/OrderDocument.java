package com.gestor.chef.gf.infrastructure.adapter.out.persistence;

import com.gestor.chef.gf.domain.model.Order;
import com.gestor.chef.gf.domain.model.OrderItem;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document(collection = "orders")
public class OrderDocument {

    @Id private String id;
    private String tableNumber;
    private String status;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private String userId;
    private String channel;
    private String notes;
    @Indexed private Instant createdAt;
    @LastModifiedDate private Instant updatedAt;

    public static OrderDocument fromDomain(Order o) {
        return OrderDocument.builder()
                .id(o.getId()).tableNumber(o.getTableNumber()).status(o.getStatus())
                .items(o.getItems()).totalAmount(o.getTotalAmount())
                .userId(o.getUserId()).channel(o.getChannel()).notes(o.getNotes())
                .createdAt(o.getCreatedAt()).updatedAt(o.getUpdatedAt())
                .build();
    }

    public Order toDomain() {
        return Order.builder()
                .id(id).tableNumber(tableNumber).status(status).items(items)
                .totalAmount(totalAmount).userId(userId).channel(channel).notes(notes)
                .createdAt(createdAt).updatedAt(updatedAt)
                .build();
    }
}
