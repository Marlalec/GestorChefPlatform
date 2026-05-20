package com.gestor.chef.gf.infrastructure.adapter.out.persistence;

import com.gestor.chef.gf.domain.model.Supplier;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document(collection = "suppliers")
public class SupplierDocument {

    @Id private String id;
    private String name;
    private String contactName;
    private String email;
    private String phone;
    private String address;
    private List<String> productIds;
    private String status;
    @CreatedDate private Instant createdAt;
    @LastModifiedDate private Instant updatedAt;

    public static SupplierDocument fromDomain(Supplier s) {
        return SupplierDocument.builder()
                .id(s.getId()).name(s.getName()).contactName(s.getContactName())
                .email(s.getEmail()).phone(s.getPhone()).address(s.getAddress())
                .productIds(s.getProductIds()).status(s.getStatus())
                .createdAt(s.getCreatedAt()).updatedAt(s.getUpdatedAt())
                .build();
    }

    public Supplier toDomain() {
        return Supplier.builder()
                .id(id).name(name).contactName(contactName).email(email)
                .phone(phone).address(address).productIds(productIds)
                .status(status).createdAt(createdAt).updatedAt(updatedAt)
                .build();
    }
}
