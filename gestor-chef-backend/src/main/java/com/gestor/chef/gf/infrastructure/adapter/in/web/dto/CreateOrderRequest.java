package com.gestor.chef.gf.infrastructure.adapter.in.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateOrderRequest {

    private String tableNumber;

    @NotEmpty(message = "La orden debe tener al menos un ítem")
    @Valid
    private List<OrderItemRequest> items;

    private String userId;
    private String notes;

    @Pattern(regexp = "DIRECT|WHATSAPP", message = "Canal inválido: usa DIRECT | WHATSAPP")
    private String channel = "DIRECT";

    @Data
    public static class OrderItemRequest {

        @NotBlank(message = "El recipeId es obligatorio")
        private String recipeId;

        private String dishName;

        @Min(value = 1, message = "La cantidad mínima es 1")
        private int quantity;

        @NotNull(message = "El precio unitario es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
        private BigDecimal unitPrice;

        private String notes;
    }
}
