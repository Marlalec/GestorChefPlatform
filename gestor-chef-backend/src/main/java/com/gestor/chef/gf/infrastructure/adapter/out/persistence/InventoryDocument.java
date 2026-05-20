package com.gestor.chef.gf.infrastructure.adapter.out.persistence;

import com.gestor.chef.gf.domain.model.Inventory;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document(collection = "inventory")
public class InventoryDocument {

    @Id private String id;
    @Indexed(unique = true) private String productId;
    private String productName;
    private String category;
    private double currentQuantity;
    private double minimumQuantity;
    private String unit;
    private boolean lowStockAlert;
    private Instant lastUpdated;

    public static InventoryDocument fromDomain(Inventory inv) {
        return InventoryDocument.builder()
                .id(inv.getId()).productId(inv.getProductId()).productName(inv.getProductName())
                .category(inv.getCategory()).currentQuantity(inv.getCurrentQuantity())
                .minimumQuantity(inv.getMinimumQuantity()).unit(inv.getUnit())
                .lowStockAlert(inv.isLowStockAlert()).lastUpdated(inv.getLastUpdated())
                .build();
    }

    public Inventory toDomain() {
        return Inventory.builder()
                .id(id).productId(productId).productName(productName).category(category)
                .currentQuantity(currentQuantity).minimumQuantity(minimumQuantity)
                .unit(unit).lowStockAlert(lowStockAlert).lastUpdated(lastUpdated)
                .build();
    }
}
