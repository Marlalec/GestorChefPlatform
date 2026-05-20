package com.gestor.chef.gf.infrastructure.adapter.in.web;

import com.gestor.chef.gf.domain.model.AlertNotification;
import com.gestor.chef.gf.domain.port.in.AlertNotificationUseCase;
import com.gestor.chef.gf.infrastructure.adapter.in.web.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
@Tag(name = "Alertas", description = "Notificaciones en tiempo real: stock bajo, vencimientos y eventos del sistema")
public class AlertNotificationController {

    private final AlertNotificationUseCase alertUseCase;

    @GetMapping
    @Operation(summary = "Listar todas las alertas")
    public ResponseEntity<ApiResponse<List<AlertNotification>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(alertUseCase.getAllAlerts()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AlertNotification>> getById(@PathVariable String id) {
        return alertUseCase.getAlertById(id)
                .map(a -> ResponseEntity.ok(ApiResponse.ok(a)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Alertas del usuario: propias + broadcasts (targetUserId null)")
    public ResponseEntity<ApiResponse<List<AlertNotification>>> getByUser(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.ok(alertUseCase.getAlertsByUser(userId)));
    }

    @GetMapping("/unread/{userId}")
    @Operation(summary = "Alertas no leídas de un usuario")
    public ResponseEntity<ApiResponse<List<AlertNotification>>> getUnread(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.ok(alertUseCase.getUnreadAlerts(userId)));
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Alertas por tipo: STOCK_LOW | EXPIRY | ORDER_UPDATE | WASTE | SYSTEM")
    public ResponseEntity<ApiResponse<List<AlertNotification>>> getByType(@PathVariable String type) {
        return ResponseEntity.ok(ApiResponse.ok(alertUseCase.getAlertsByType(type)));
    }

    @PostMapping
    @Operation(summary = "Crear alerta manual (broadcast si targetUserId es null)")
    public ResponseEntity<ApiResponse<AlertNotification>> create(@RequestBody AlertNotification alert) {
        return ResponseEntity.status(201).body(ApiResponse.ok("Alerta creada", alertUseCase.createAlert(alert)));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Marcar alerta como leída")
    public ResponseEntity<ApiResponse<AlertNotification>> markAsRead(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(alertUseCase.markAsRead(id)));
    }

    @PatchMapping("/read-all/{userId}")
    @Operation(summary = "Marcar todas las alertas del usuario como leídas")
    public ResponseEntity<ApiResponse<Void>> markAllRead(@PathVariable String userId) {
        alertUseCase.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.ok("Todas las alertas marcadas como leídas", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        alertUseCase.deleteAlert(id);
        return ResponseEntity.ok(ApiResponse.ok("Alerta eliminada", null));
    }

    @PostMapping("/generate-stock")
    @Operation(summary = "Disparar revisión de stock bajo y generar alertas STOCK_LOW")
    public ResponseEntity<ApiResponse<Void>> generateStockAlerts() {
        alertUseCase.generateStockAlerts();
        return ResponseEntity.ok(ApiResponse.ok("Alertas de stock generadas", null));
    }

    @PostMapping("/generate-expiry")
    @Operation(summary = "Disparar revisión de vencimientos y generar alertas EXPIRY")
    public ResponseEntity<ApiResponse<Void>> generateExpiryAlerts() {
        alertUseCase.generateExpiryAlerts();
        return ResponseEntity.ok(ApiResponse.ok("Alertas de vencimiento generadas", null));
    }
}
