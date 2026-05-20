package com.gestor.chef.gf.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {
    private String id;
    private String dishName;
    private String category;
    private String description;
    private List<RecipeIngredient> ingredientsList;
    private BigDecimal price;
    private int preparationTimeMinutes;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;
}
