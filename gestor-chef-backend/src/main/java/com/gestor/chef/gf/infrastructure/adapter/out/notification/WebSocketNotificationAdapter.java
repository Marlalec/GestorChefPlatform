package com.gestor.chef.gf.infrastructure.adapter.out.notification;

import com.gestor.chef.gf.domain.model.AlertNotification;
import com.gestor.chef.gf.domain.model.Order;
import com.gestor.chef.gf.domain.port.out.RealtimeNotificationPort;
import com.gestor.chef.gf.infrastructure.adapter.in.web.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketNotificationAdapter implements RealtimeNotificationPort {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendAlert(AlertNotification alert) {
        if (alert.getTargetUserId() != null && !alert.getTargetUserId().isBlank()) {
            messagingTemplate.convertAndSendToUser(
                    alert.getTargetUserId(),
                    "/queue/notifications",
                    ApiResponse.ok("Nueva alerta", alert)
            );
            return;
        }
        messagingTemplate.convertAndSend("/topic/alerts", ApiResponse.ok("Nueva alerta", alert));
    }

    @Override
    public void sendOrderUpdate(Order order) {
        messagingTemplate.convertAndSend("/topic/orders", ApiResponse.ok("Orden actualizada", order));
    }
}
