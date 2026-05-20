package com.gestor.chef.gf.infrastructure.adapter.out.persistence;

import com.gestor.chef.gf.domain.model.User;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document(collection = "users")
public class UserDocument {

    @Id private String id;
    @Indexed(unique = true) private String email;
    private String password;
    private String name;
    private String phone;
    private String rol;
    private String accountStatus;
    @CreatedDate private Instant createdAt;
    @LastModifiedDate private Instant updatedAt;

    public static UserDocument fromDomain(User u) {
        return UserDocument.builder()
                .id(u.getId()).email(u.getEmail()).password(u.getPassword())
                .name(u.getName()).phone(u.getPhone()).rol(u.getRol())
                .accountStatus(u.getAccountStatus())
                .createdAt(u.getCreatedAt()).updatedAt(u.getUpdatedAt())
                .build();
    }

    public User toDomain() {
        return User.builder()
                .id(id).email(email).password(password).name(name).phone(phone)
                .rol(rol).accountStatus(accountStatus)
                .createdAt(createdAt).updatedAt(updatedAt)
                .build();
    }
}
