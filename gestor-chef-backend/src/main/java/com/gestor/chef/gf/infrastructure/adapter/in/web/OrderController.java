package com.gestor.chef.gf.infrastructure.adapter.in.web;

import com.gestor.chef.gf.domain.model.Order;
import com.gestor.chef.gf.domain.model.OrderItem;
import com.gestor.chef.gf.domain.port.in.OrderUseCase;
import com.gestor.chef.gf.infrastructure.adapter.in.web.dto.ApiResponse;
import com.gestor.chef.gf.infrastructure.adapter.in.web.dto.CreateOrderRequest;
import com.gestor.chef.gf.infrastructure.adapter.in.web.dto.StatusUpdateRequest;
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
import java.util.stream.Collectors;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Órdenes / Mesas", description = "Gestión de comandas: mesas, pedidos, estados y canal WhatsApp")
public class OrderController {

    private final OrderUseCase orderUseCase;

    @GetMapping
    @Operation(summary = "Listar todas las órdenes (paginado). Parámetros: page, size, sort")
    public ResponseEntity<ApiResponse<Page<Order>>> getAll(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(orderUseCase.getAllOrdersPaged(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>> getById(@PathVariable String id) {
        return orderUseCase.getOrderById(id)
                .map(o -> ResponseEntity.ok(ApiResponse.ok(o)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear nueva comanda/orden de mesa")
    public ResponseEntity<ApiResponse<Order>> create(@Valid @RequestBody CreateOrderRequest req) {
        List<OrderItem> items = req.getItems().stream()
                .map(i -> OrderItem.builder()
                        .recipeId(i.getRecipeId()).dishName(i.getDishName())
                        .quantity(i.getQuantity()).unitPrice(i.getUnitPrice())
                        .notes(i.getNotes()).build())
                .collect(Collectors.toList());
        Order order = Order.builder()
                .tableNumber(req.getTableNumber()).items(items)
                .userId(req.getUserId()).channel(req.getChannel())
                .notes(req.getNotes()).build();
        return ResponseEntity.status(201).body(ApiResponse.ok("Orden creada", orderUseCase.createOrder(order)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>> update(@PathVariable String id,
                                                      @RequestBody CreateOrderRequest req) {
        List<OrderItem> items = req.getItems() == null ? null : req.getItems().stream()
                .map(i -> OrderItem.builder()
                        .recipeId(i.getRecipeId()).dishName(i.getDishName())
                        .quantity(i.getQuantity()).unitPrice(i.getUnitPrice())
                        .notes(i.getNotes()).build())
                .collect(Collectors.toList());
        Order order = Order.builder()
                .tableNumber(req.getTableNumber()).items(items).notes(req.getNotes()).build();
        return ResponseEntity.ok(ApiResponse.ok(orderUseCase.updateOrder(id, order)));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Órdenes por estado: PENDING | IN_PROGRESS | COMPLETED | CANCELLED")
    public ResponseEntity<ApiResponse<List<Order>>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(ApiResponse.ok(orderUseCase.getOrdersByStatus(status)));
    }

    @GetMapping("/table/{tableNumber}")
    @Operation(summary = "Órdenes de una mesa específica")
    public ResponseEntity<ApiResponse<List<Order>>> getByTable(@PathVariable String tableNumber) {
        return ResponseEntity.ok(ApiResponse.ok(orderUseCase.getOrdersByTable(tableNumber)));
    }

    @GetMapping("/range")
    @Operation(summary = "Órdenes en rango de fechas")
    public ResponseEntity<ApiResponse<List<Order>>> getByDateRange(
            @RequestParam Instant from, @RequestParam Instant to) {
        return ResponseEntity.ok(ApiResponse.ok(orderUseCase.getOrdersByDateRange(from, to)));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Cambiar estado — al completar descuenta ingredientes del inventario")
    public ResponseEntity<ApiResponse<Order>> updateStatus(@PathVariable String id,
                                                            @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(
                orderUseCase.updateOrderStatus(id, request.getStatus(),
                        request.getUserId() != null ? request.getUserId() : "system")));
    }

    @DeleteMapping("/{id}/cancel")
    @Operation(summary = "Cancelar una orden")
    public ResponseEntity<ApiResponse<Void>> cancel(@PathVariable String id,
                                                     @RequestParam(defaultValue = "system") String userId) {
        orderUseCase.cancelOrder(id, userId);
        return ResponseEntity.ok(ApiResponse.ok("Orden cancelada", null));
    }

    @PostMapping("/whatsapp")
    @Operation(summary = "Procesar pedido recibido por WhatsApp")
    public ResponseEntity<ApiResponse<Order>> processWhatsApp(@RequestBody Map<String, String> body) {
        return ResponseEntity.status(201).body(
                ApiResponse.ok("Pedido WhatsApp procesado",
                        orderUseCase.processWhatsAppOrder(
                                body.getOrDefault("message", ""),
                                body.getOrDefault("senderPhone", "unknown"))));
    }
}
