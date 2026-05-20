package com.gestor.chef.gf.infrastructure.adapter.out.persistence;

import com.gestor.chef.gf.domain.model.InventoryMovement;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document(collection = "inventory_movements")
public class InventoryMovementDocument {

    @Id private String id;
    @Indexed private String productId;
    private String productName;
    private double quantityChanged;
    private String movementType;
    private String reason;
    @Indexed private String userId;
    private String userName;
    private Double weightFromScale;
    private String notes;
    @Indexed private Instant timestamp;

    public static InventoryMovementDocument fromDomain(InventoryMovement m) {
        return InventoryMovementDocument.builder()
                .id(m.getId()).productId(m.getProductId()).productName(m.getProductName())
                .quantityChanged(m.getQuantityChanged()).movementType(m.getMovementType())
                .reason(m.getReason()).userId(m.getUserId()).userName(m.getUserName())
                .weightFromScale(m.getWeightFromScale()).notes(m.getNotes()).timestamp(m.getTimestamp())
                .build();
    }

    public InventoryMovement toDomain() {
        return InventoryMovement.builder()
                .id(id).productId(productId).productName(productName)
                .quantityChanged(quantityChanged).movementType(movementType)
                .reason(reason).userId(userId).userName(userName)
                .weightFromScale(weightFromScale).notes(notes).timestamp(timestamp)
                .build();
    }
}
