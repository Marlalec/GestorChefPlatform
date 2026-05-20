package com.gestor.chef.gf.infrastructure.adapter.in.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateRecipeRequest {

    @NotBlank(message = "El nombre del plato es obligatorio")
    private String dishName;

    @NotBlank(message = "La categoría es obligatoria")
    @Pattern(regexp = "ENTRADA|PLATO_PRINCIPAL|POSTRE|BEBIDA",
             message = "Categoría inválida: usa ENTRADA | PLATO_PRINCIPAL | POSTRE | BEBIDA")
    private String category;

    private String description;

    @NotEmpty(message = "La receta debe tener al menos un ingrediente")
    @Valid
    private List<IngredientRequest> ingredientsList;

    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal price;

    @Min(value = 1, message = "El tiempo de preparación mínimo es 1 minuto")
    private int preparationTimeMinutes;

    @Data
    public static class IngredientRequest {

        @NotBlank(message = "El productId del ingrediente es obligatorio")
        private String productId;

        private String productName;

        @DecimalMin(value = "0.0", inclusive = false, message = "La cantidad debe ser mayor a 0")
        private double amount;

        private String unit;
    }
}
