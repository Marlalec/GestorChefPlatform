package com.gestor.chef.gf.domain.port.out;

import com.gestor.chef.gf.domain.model.Order;

public interface CustomerNotificationPort {
    void sendOrderConfirmation(Order order, String customerPhone);
    void sendOrderStatusUpdate(Order order, String customerPhone, String newStatus);
}
