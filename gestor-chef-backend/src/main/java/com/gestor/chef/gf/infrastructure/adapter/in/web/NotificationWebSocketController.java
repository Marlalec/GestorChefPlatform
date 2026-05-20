package com.gestor.chef.gf.infrastructure.adapter.in.web;

import com.gestor.chef.gf.domain.model.AlertNotification;
import com.gestor.chef.gf.domain.model.value.DomainValues;
import com.gestor.chef.gf.domain.port.in.AlertNotificationUseCase;
import com.gestor.chef.gf.infrastructure.adapter.in.web.dto.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Tag(name = "WebSocket STOMP", description = "Mensajería en tiempo real: alertas, órdenes y notificaciones personales")
public class NotificationWebSocketController {

    private final AlertNotificationUseCase alertUseCase;

    @MessageMapping("/ping")
    @SendToUser("/queue/notifications")
    public Map<String, String> ping() {
        return Map.of("type", "PONG", "message", "Connection alive");
    }

    @MessageMapping("/subscribe-user")
    @SendToUser("/queue/notifications")
    public ApiResponse<?> subscribeUser(Map<String, String> payload) {
        String userId = payload.getOrDefault("userId", "");
        if (userId.isBlank()) {
            return ApiResponse.error("userId requerido");
        }
        return ApiResponse.ok("alertas_pendientes", alertUseCase.getUnreadAlerts(userId));
    }

    @MessageMapping("/alert-read")
    @SendToUser("/queue/notifications")
    public ApiResponse<?> markAlertRead(Map<String, String> payload) {
        String alertId = payload.getOrDefault("alertId", "");
        if (alertId.isBlank()) {
            return ApiResponse.error("alertId requerido");
        }
        AlertNotification updated = alertUseCase.markAsRead(alertId);
        return ApiResponse.ok("Alerta marcada como leída", updated);
    }

    @MessageMapping("/broadcast")
    @SendTo("/topic/alerts")
    public Map<String, String> broadcast(Map<String, String> payload) {
        return Map.of(
                "type", DomainValues.AlertType.SYSTEM,
                "message", payload.getOrDefault("message", "Mensaje del sistema"),
                "timestamp", Instant.now().toString()
        );
    }
}
