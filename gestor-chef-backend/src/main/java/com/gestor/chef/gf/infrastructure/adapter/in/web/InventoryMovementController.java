package com.gestor.chef.gf.infrastructure.adapter.in.web;

import com.gestor.chef.gf.domain.model.InventoryMovement;
import com.gestor.chef.gf.domain.port.in.InventoryMovementUseCase;
import com.gestor.chef.gf.infrastructure.adapter.in.web.dto.ApiResponse;
import com.gestor.chef.gf.infrastructure.adapter.in.web.dto.CreateInventoryMovementRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/inventory-movements")
@RequiredArgsConstructor
@Tag(name = "Movimientos de Inventario", description = "Registro de entradas, salidas, mermas y pesajes")
public class InventoryMovementController {

    private final InventoryMovementUseCase movementUseCase;

    @GetMapping
    @Operation(summary = "Listar todos los movimientos (paginado). Parámetros: page, size, sort")
    public ResponseEntity<ApiResponse<Page<InventoryMovement>>> getAll(
            @PageableDefault(size = 50, sort = "timestamp") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(movementUseCase.getAllMovementsPaged(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InventoryMovement>> getById(@PathVariable String id) {
        return movementUseCase.getMovementById(id)
                .map(m -> ResponseEntity.ok(ApiResponse.ok(m)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Registrar movimiento — actualiza stock automáticamente",
               description = "movementType: IN | OUT | WASTE. reason: PURCHASE | RECIPE_USE | WASTE | SCALE_MEASUREMENT | ADJUSTMENT")
    public ResponseEntity<ApiResponse<InventoryMovement>> register(
            @Valid @RequestBody CreateInventoryMovementRequest req) {
        InventoryMovement movement = InventoryMovement.builder()
                .productId(req.getProductId()).quantityChanged(req.getQuantityChanged())
                .movementType(req.getMovementType()).reason(req.getReason())
                .userId(req.getUserId()).weightFromScale(req.getWeightFromScale())
                .notes(req.getNotes()).build();
        return ResponseEntity.status(201).body(
                ApiResponse.ok("Movimiento registrado", movementUseCase.registerMovement(movement)));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Movimientos por producto")
    public ResponseEntity<ApiResponse<List<InventoryMovement>>> getByProduct(@PathVariable String productId) {
        return ResponseEntity.ok(ApiResponse.ok(movementUseCase.getMovementsByProduct(productId)));
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Movimientos por tipo: IN | OUT | WASTE")
    public ResponseEntity<ApiResponse<List<InventoryMovement>>> getByType(@PathVariable String type) {
        return ResponseEntity.ok(ApiResponse.ok(movementUseCase.getMovementsByType(type)));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Movimientos por usuario (auditoría)")
    public ResponseEntity<ApiResponse<List<InventoryMovement>>> getByUser(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.ok(movementUseCase.getMovementsByUser(userId)));
    }

    @GetMapping("/range")
    @Operation(summary = "Movimientos en rango de fechas (ISO 8601)")
    public ResponseEntity<ApiResponse<List<InventoryMovement>>> getByDateRange(
            @RequestParam Instant from, @RequestParam Instant to) {
        return ResponseEntity.ok(ApiResponse.ok(movementUseCase.getMovementsByDateRange(from, to)));
    }

    @PostMapping("/scale")
    @Operation(summary = "Registrar peso desde balanza de precisión (hardware)")
    public ResponseEntity<ApiResponse<InventoryMovement>> registerFromScale(
            @RequestBody Map<String, Object> body) {
        String productId = (String) body.get("productId");
        double weight = Double.parseDouble(body.get("weight").toString());
        String userId = (String) body.get("userId");
        return ResponseEntity.status(201).body(
                ApiResponse.ok("Pesaje registrado", movementUseCase.registerScaleWeight(productId, weight, userId)));
    }
}
