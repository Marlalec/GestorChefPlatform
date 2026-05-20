package com.gestor.chef.gf.infrastructure.adapter.out.persistence;

import com.gestor.chef.gf.domain.model.Waste;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigDecimal;
import java.time.Instant;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document(collection = "wastes")
public class WasteDocument {

    @Id private String id;

    @Indexed private String productId;
    private String productName;
    private double quantityWasted;
    private String unit;

    @Indexed private String cause;
    private BigDecimal estimatedCost;
    private String description;

    @Indexed private String reportedBy;
    private String reportedByName;

    @Indexed private Instant occurredAt;
    private Instant registeredAt;
    private String inventoryMovementId;

    public static WasteDocument fromDomain(Waste w) {
        return WasteDocument.builder()
                .id(w.getId())
                .productId(w.getProductId())
                .productName(w.getProductName())
                .quantityWasted(w.getQuantityWasted())
                .unit(w.getUnit())
                .cause(w.getCause())
                .estimatedCost(w.getEstimatedCost())
                .description(w.getDescription())
                .reportedBy(w.getReportedBy())
                .reportedByName(w.getReportedByName())
                .occurredAt(w.getOccurredAt())
                .registeredAt(w.getRegisteredAt())
                .inventoryMovementId(w.getInventoryMovementId())
                .build();
    }

    public Waste toDomain() {
        return Waste.builder()
                .id(id)
                .productId(productId)
                .productName(productName)
                .quantityWasted(quantityWasted)
                .unit(unit)
                .cause(cause)
                .estimatedCost(estimatedCost)
                .description(description)
                .reportedBy(reportedBy)
                .reportedByName(reportedByName)
                .occurredAt(occurredAt)
                .registeredAt(registeredAt)
                .inventoryMovementId(inventoryMovementId)
                .build();
    }
}
