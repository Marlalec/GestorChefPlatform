package com.gestor.chef.gf.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateProductRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "La categoría es obligatoria")
    @Pattern(regexp = "CARNES|VERDURAS|LACTEOS|BEBIDAS|OTROS",
             message = "Categoría inválida: usa CARNES | VERDURAS | LACTEOS | BEBIDAS | OTROS")
    private String category;

    private String supplierId;
    private String supplierName;

    @PositiveOrZero(message = "La cantidad no puede ser negativa")
    private double quantity;

    @PositiveOrZero(message = "El mínimo no puede ser negativo")
    private double minimumQuantity;

    @NotBlank(message = "La unidad es obligatoria")
    @Pattern(regexp = "kg|g|l|ml|unidades", message = "Unidad inválida: usa kg | g | l | ml | unidades")
    private String unit;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal price;

    private LocalDate expirationDate;
    private String description;
}
