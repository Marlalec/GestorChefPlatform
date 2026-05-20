package com.gestor.chef.gf.infrastructure.adapter.in.web;

import com.gestor.chef.gf.domain.model.Waste;
import com.gestor.chef.gf.domain.port.in.WasteUseCase;
import com.gestor.chef.gf.infrastructure.adapter.in.web.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/wastes")
@RequiredArgsConstructor
@Tag(name = "Desperdicios", description = "Registro y consulta de desperdicios de inventario. " +
        "Al registrar un desperdicio se crea automaticamente un movimiento de inventario de tipo WASTE.")
@SecurityRequirement(name = "bearerAuth")
public class WasteController {

    private final WasteUseCase wasteUseCase;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','COCINA')")
    @Operation(
        summary = "Registrar desperdicio",
        description = "Crea un registro de desperdicio y genera automaticamente un movimiento de inventario " +
                      "de tipo WASTE. El campo estimatedCost se calcula con el precio actual del producto " +
                      "si no se envia. Causas validas: EXPIRY, DETERIORATION, KITCHEN_ACCIDENT, OTHER. " +
                      "Roles: ADMIN, COCINA."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Desperdicio registrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos invalidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Rol sin permiso")
    })
    public ResponseEntity<ApiResponse<Waste>> register(
            @RequestBody Waste waste,
            @AuthenticationPrincipal UserDetails principal) {

        if (waste.getReportedBy() == null && principal != null) {
            waste.setReportedBy(principal.getUsername());
        }
        Waste saved = wasteUseCase.registerWaste(waste);
        return ResponseEntity.status(201).body(ApiResponse.ok("Desperdicio registrado", saved));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','COCINA','CONTABLE')")
    @Operation(
        summary = "Listar desperdicios (paginado)",
        description = "Devuelve todos los desperdicios ordenados por fecha de ocurrencia. " +
                      "Parametros de paginacion: page, size, sort. Roles: ADMIN, COCINA, CONTABLE."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista retornada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido")
    })
    public ResponseEntity<ApiResponse<Page<Waste>>> getAll(
            @PageableDefault(size = 20, sort = "occurredAt") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(wasteUseCase.getAllWastesPaged(pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','COCINA','CONTABLE')")
    @Operation(summary = "Obtener detalle de un desperdicio por ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Desperdicio encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No encontrado")
    })
    public ResponseEntity<ApiResponse<Waste>> getById(
            @Parameter(description = "ID del desperdicio") @PathVariable String id) {
        return wasteUseCase.getWasteById(id)
                .map(w -> ResponseEntity.ok(ApiResponse.ok(w)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN','COCINA','CONTABLE')")
    @Operation(summary = "Desperdicios por producto", description = "Filtra todos los desperdicios asociados a un producto dado.")
    public ResponseEntity<ApiResponse<List<Waste>>> getByProduct(
            @Parameter(description = "ID del producto") @PathVariable String productId) {
        return ResponseEntity.ok(ApiResponse.ok(wasteUseCase.getWastesByProduct(productId)));
    }

    @GetMapping("/cause/{cause}")
    @PreAuthorize("hasAnyRole('ADMIN','COCINA','CONTABLE')")
    @Operation(
        summary = "Desperdicios por causa",
        description = "Filtra desperdicios por tipo de causa. " +
                      "Valores validos: EXPIRY, DETERIORATION, KITCHEN_ACCIDENT, OTHER."
    )
    public ResponseEntity<ApiResponse<List<Waste>>> getByCause(
            @Parameter(description = "Causa del desperdicio: EXPIRY | DETERIORATION | KITCHEN_ACCIDENT | OTHER")
            @PathVariable String cause) {
        return ResponseEntity.ok(ApiResponse.ok(wasteUseCase.getWastesByCause(cause.toUpperCase())));
    }

    @GetMapping("/range")
    @PreAuthorize("hasAnyRole('ADMIN','COCINA','CONTABLE')")
    @Operation(
        summary = "Desperdicios por rango de fechas",
        description = "Filtra desperdicios entre dos fechas. " +
                      "Formato ISO 8601, por ejemplo: 2025-05-01T00:00:00Z"
    )
    public ResponseEntity<ApiResponse<List<Waste>>> getByRange(
            @Parameter(description = "Fecha de inicio (ISO 8601), ej: 2025-05-01T00:00:00Z") @RequestParam String from,
            @Parameter(description = "Fecha de fin (ISO 8601), ej: 2025-05-31T23:59:59Z")   @RequestParam String to) {
        return ResponseEntity.ok(ApiResponse.ok(
                wasteUseCase.getWastesByDateRange(Instant.parse(from), Instant.parse(to))));
    }
}
