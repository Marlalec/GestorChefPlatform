package com.gestor.chef.gf.domain.port.out;

import com.gestor.chef.gf.domain.model.AlertNotification;
import com.gestor.chef.gf.domain.model.Order;

public interface RealtimeNotificationPort {
    void sendAlert(AlertNotification alert);
    void sendOrderUpdate(Order order);
}
