package com.gestor.chef.gf.domain.port.in;

import com.gestor.chef.gf.domain.model.AlertNotification;
import java.util.List;
import java.util.Optional;

public interface AlertNotificationUseCase {
    AlertNotification createAlert(AlertNotification alert);
    Optional<AlertNotification> getAlertById(String id);
    List<AlertNotification> getAllAlerts();
    List<AlertNotification> getAlertsByUser(String userId);
    List<AlertNotification> getUnreadAlerts(String userId);
    List<AlertNotification> getAlertsByType(String type);
    AlertNotification markAsRead(String id);
    void markAllAsRead(String userId);
    void deleteAlert(String id);
    void generateStockAlerts();
    void generateExpiryAlerts();
}
