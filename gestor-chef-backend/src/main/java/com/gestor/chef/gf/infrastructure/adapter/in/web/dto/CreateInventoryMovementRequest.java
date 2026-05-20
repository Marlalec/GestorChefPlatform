package com.gestor.chef.gf.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateInventoryMovementRequest {

    @NotBlank(message = "El productId es obligatorio")
    private String productId;

    @DecimalMin(value = "0.0", inclusive = false, message = "La cantidad debe ser mayor a 0")
    private double quantityChanged;

    @NotBlank(message = "El tipo de movimiento es obligatorio")
    @Pattern(regexp = "IN|OUT|WASTE", message = "Tipo inválido: usa IN | OUT | WASTE")
    private String movementType;

    @NotBlank(message = "La razón del movimiento es obligatoria")
    @Pattern(regexp = "PURCHASE|RECIPE_USE|WASTE|SCALE_MEASUREMENT|ADJUSTMENT",
             message = "Razón inválida: usa PURCHASE | RECIPE_USE | WASTE | SCALE_MEASUREMENT | ADJUSTMENT")
    private String reason;

    private String userId;
    private Double weightFromScale;
    private String notes;
}
