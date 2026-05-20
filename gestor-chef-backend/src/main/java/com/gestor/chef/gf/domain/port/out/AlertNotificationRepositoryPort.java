package com.gestor.chef.gf.domain.port.out;

import com.gestor.chef.gf.domain.model.AlertNotification;
import java.util.List;
import java.util.Optional;

public interface AlertNotificationRepositoryPort {
    AlertNotification save(AlertNotification alert);
    Optional<AlertNotification> findById(String id);
    List<AlertNotification> findAll();
    List<AlertNotification> findByTargetUserIdOrBroadcast(String userId);
    List<AlertNotification> findByStatusAndUserId(String status, String userId);
    List<AlertNotification> findByType(String type);
    void deleteById(String id);
    void markAllAsReadByUserId(String userId);
    boolean existsOpenAlert(String productId, String type);
}
