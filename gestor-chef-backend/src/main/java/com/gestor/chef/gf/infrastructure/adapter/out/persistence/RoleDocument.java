package com.gestor.chef.gf.infrastructure.adapter.out.persistence;

import com.gestor.chef.gf.domain.model.Role;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document(collection = "roles")
public class RoleDocument {

    @Id private String id;
    @Indexed(unique = true) private String name;
    private String description;
    private List<String> permissions;

    public static RoleDocument fromDomain(Role r) {
        return RoleDocument.builder().id(r.getId()).name(r.getName())
                .description(r.getDescription()).permissions(r.getPermissions()).build();
    }

    public Role toDomain() {
        return Role.builder().id(id).name(name).description(description).permissions(permissions).build();
    }
}
