package com.gestor.chef.gf.infrastructure.adapter.in.web;

import com.gestor.chef.gf.domain.model.Inventory;
import com.gestor.chef.gf.domain.port.in.InventoryUseCase;
import com.gestor.chef.gf.infrastructure.adapter.in.web.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventario", description = "Estado actual del inventario y KPIs del dashboard")
public class InventoryController {

    private final InventoryUseCase inventoryUseCase;

    @GetMapping
    @Operation(summary = "Estado completo del inventario")
    public ResponseEntity<ApiResponse<List<Inventory>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(inventoryUseCase.getAllInventory()));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<Inventory>> getByProduct(@PathVariable String productId) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryUseCase.getInventoryByProduct(productId)));
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Items con alerta de stock bajo")
    public ResponseEntity<ApiResponse<List<Inventory>>> getLowStock() {
        return ResponseEntity.ok(ApiResponse.ok(inventoryUseCase.getLowStockItems()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Inventory>> create(@RequestBody Inventory inventory) {
        return ResponseEntity.status(201).body(ApiResponse.ok(inventoryUseCase.createInventoryEntry(inventory)));
    }

    @PatchMapping("/product/{productId}")
    @Operation(summary = "Actualizar cantidad actual del inventario")
    public ResponseEntity<ApiResponse<Inventory>> update(
            @PathVariable String productId,
            @RequestBody Map<String, Double> body) {
        Double qty = body.get("quantity");
        if (qty == null) return ResponseEntity.badRequest().body(ApiResponse.error("Falta el campo 'quantity'"));
        return ResponseEntity.ok(ApiResponse.ok(inventoryUseCase.updateInventory(productId, qty)));
    }

    @PostMapping("/sync")
    @Operation(summary = "Sincronizar tabla de inventario desde colección de productos")
    public ResponseEntity<ApiResponse<Void>> sync() {
        inventoryUseCase.syncFromProducts();
        return ResponseEntity.ok(ApiResponse.ok("Inventario sincronizado desde productos", null));
    }

    @GetMapping("/kpis")
    @Operation(summary = "KPIs del dashboard: totalSkus, totalUnidades, stockBajo, valorTotal")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getKpis() {
        List<Inventory> all = inventoryUseCase.getAllInventory();
        long totalSkus = all.size();
        double totalUnidades = all.stream().mapToDouble(Inventory::getCurrentQuantity).sum();
        long stockBajo = all.stream().filter(Inventory::isLowStockAlert).count();
        Map<String, Object> kpis = Map.of(
                "totalSkus", totalSkus,
                "totalUnidades", totalUnidades,
                "stockBajo", stockBajo,
                "totalItems", all.size()
        );
        return ResponseEntity.ok(ApiResponse.ok(kpis));
    }
}
