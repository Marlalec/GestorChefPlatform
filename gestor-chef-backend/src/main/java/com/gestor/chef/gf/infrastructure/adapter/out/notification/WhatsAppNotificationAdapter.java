package com.gestor.chef.gf.infrastructure.adapter.out.notification;

import com.gestor.chef.gf.domain.model.Order;
import com.gestor.chef.gf.domain.port.out.CustomerNotificationPort;
import com.gestor.chef.gf.infrastructure.external.WhatsAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WhatsAppNotificationAdapter implements CustomerNotificationPort {

    private final WhatsAppService whatsAppService;

    @Override
    public void sendOrderConfirmation(Order order, String customerPhone) {
        whatsAppService.sendOrderConfirmation(order, customerPhone);
    }

    @Override
    public void sendOrderStatusUpdate(Order order, String customerPhone, String newStatus) {
        whatsAppService.sendOrderStatusUpdate(order, customerPhone, newStatus);
    }
}
