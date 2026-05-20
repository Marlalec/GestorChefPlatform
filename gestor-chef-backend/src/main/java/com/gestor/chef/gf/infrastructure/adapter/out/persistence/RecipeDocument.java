package com.gestor.chef.gf.infrastructure.adapter.out.persistence;

import com.gestor.chef.gf.domain.model.Recipe;
import com.gestor.chef.gf.domain.model.RecipeIngredient;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document(collection = "recipes")
public class RecipeDocument {

    @Id private String id;
    private String dishName;
    private String category;
    private String description;
    private List<RecipeIngredient> ingredientsList;
    private BigDecimal price;
    private int preparationTimeMinutes;
    private String status;
    private String note;
    @CreatedDate private Instant createdAt;
    @LastModifiedDate private Instant updatedAt;

    public static RecipeDocument fromDomain(Recipe r) {
        return RecipeDocument.builder()
                .id(r.getId()).dishName(r.getDishName()).category(r.getCategory())
                .description(r.getDescription()).ingredientsList(r.getIngredientsList())
                .price(r.getPrice()).preparationTimeMinutes(r.getPreparationTimeMinutes())
                .status(r.getStatus()).createdAt(r.getCreatedAt()).updatedAt(r.getUpdatedAt())
                .build();
    }

    public Recipe toDomain() {
        return Recipe.builder()
                .id(id).dishName(dishName).category(category).description(description)
                .ingredientsList(ingredientsList).price(price)
                .preparationTimeMinutes(preparationTimeMinutes)
                .status(status).createdAt(createdAt).updatedAt(updatedAt)
                .build();
    }
}
