package com.gestor.chef.gf.infrastructure.adapter.out.persistence;

import com.gestor.chef.gf.domain.model.Product;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document(collection = "products")
public class ProductDocument {

    @Id private String id;
    private String name;
    private String category;
    private String supplierId;
    private String supplierName;
    private double quantity;
    private double minimumQuantity;
    private String unit;
    private BigDecimal price;
    private LocalDate expirationDate;
    private String status;
    private String description;
    @CreatedDate private Instant createdAt;
    @LastModifiedDate private Instant updatedAt;

    public static ProductDocument fromDomain(Product p) {
        return ProductDocument.builder()
                .id(p.getId()).name(p.getName()).category(p.getCategory())
                .supplierId(p.getSupplierId()).supplierName(p.getSupplierName())
                .quantity(p.getQuantity()).minimumQuantity(p.getMinimumQuantity())
                .unit(p.getUnit()).price(p.getPrice())
                .expirationDate(p.getExpirationDate())
                .status(p.getStatus()).description(p.getDescription())
                .createdAt(p.getCreatedAt()).updatedAt(p.getUpdatedAt())
                .build();
    }

    public Product toDomain() {
        return Product.builder()
                .id(id).name(name).category(category)
                .supplierId(supplierId).supplierName(supplierName)
                .quantity(quantity).minimumQuantity(minimumQuantity)
                .unit(unit).price(price).expirationDate(expirationDate)
                .status(status).description(description)
                .createdAt(createdAt).updatedAt(updatedAt)
                .build();
    }
}
