package com.gestor.chef.gf.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateSupplierRequest {

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    private String name;

    private String contactName;

    @Email(message = "El email no es válido")
    private String email;

    private String phone;
    private String address;
}
