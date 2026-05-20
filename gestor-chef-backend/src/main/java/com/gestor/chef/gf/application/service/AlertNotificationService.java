package com.gestor.chef.gf.application.service;

import com.gestor.chef.gf.application.service.support.EntityFinder;
import com.gestor.chef.gf.domain.model.AlertNotification;
import com.gestor.chef.gf.domain.model.Product;
import com.gestor.chef.gf.domain.model.value.DomainValues;
import com.gestor.chef.gf.domain.port.in.AlertNotificationUseCase;
import com.gestor.chef.gf.domain.port.in.ProductUseCase;
import com.gestor.chef.gf.domain.port.out.AlertNotificationRepositoryPort;
import com.gestor.chef.gf.domain.port.out.RealtimeNotificationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertNotificationService implements AlertNotificationUseCase {

    private final AlertNotificationRepositoryPort alertRepository;
    private final ProductUseCase productUseCase;
    private final RealtimeNotificationPort realtimeNotificationPort;

    @Value("${app.alerts.expiry-days:7}")
    private int expiryDays;

    @Override
    public AlertNotification createAlert(AlertNotification alert) {
        alert.setStatus(DomainValues.Status.UNREAD);
        alert.setCreatedAt(Instant.now());
        AlertNotification saved = alertRepository.save(alert);
        realtimeNotificationPort.sendAlert(saved);
        return saved;
    }

    @Override
    public Optional<AlertNotification> getAlertById(String id) {
        return alertRepository.findById(id);
    }

    @Override
    public List<AlertNotification> getAllAlerts() {
        return alertRepository.findAll();
    }

    @Override
    public List<AlertNotification> getAlertsByUser(String userId) {
        return alertRepository.findByTargetUserIdOrBroadcast(userId);
    }

    @Override
    public List<AlertNotification> getUnreadAlerts(String userId) {
        return alertRepository.findByStatusAndUserId(DomainValues.Status.UNREAD, userId);
    }

    @Override
    public List<AlertNotification> getAlertsByType(String type) {
        return alertRepository.findByType(type);
    }

    @Override
    public AlertNotification markAsRead(String id) {
        AlertNotification alert = EntityFinder.required(alertRepository.findById(id), "Alerta", id);
        alert.setStatus(DomainValues.Status.READ);
        alert.setReadAt(Instant.now());
        return alertRepository.save(alert);
    }

    @Override
    public void markAllAsRead(String userId) {
        alertRepository.markAllAsReadByUserId(userId);
    }

    @Override
    public void deleteAlert(String id) {
        alertRepository.deleteById(id);
    }

    @Override
    @Scheduled(fixedDelayString = "${app.alerts.stock-check-ms:300000}")
    public void generateStockAlerts() {
        try {
            productUseCase.getLowStockProducts().forEach(this::createStockAlert);
        } catch (Exception ex) {
            log.warn("generateStockAlerts falló. Se reintentará en el próximo ciclo. Causa: {}", ex.getMessage());
        }
    }

    @Override
    @Scheduled(cron = "0 0 8 * * ?")
    public void generateExpiryAlerts() {
        try {
            LocalDate threshold = LocalDate.now().plusDays(expiryDays);
            productUseCase.getExpiringProducts(threshold).forEach(this::createExpiryAlert);
        } catch (Exception ex) {
            log.warn("generateExpiryAlerts falló. Se reintentará en el próximo ciclo. Causa: {}", ex.getMessage());
        }
    }

    private void createStockAlert(Product product) {
        if (alertRepository.existsOpenAlert(product.getId(), DomainValues.AlertType.STOCK_LOW)) {
            return;
        }
        AlertNotification alert = AlertNotification.builder()
                .type(DomainValues.AlertType.STOCK_LOW)
                .title("⚠️ Stock Bajo: " + product.getName())
                .message(String.format(
                        "Stock bajo detectado para %s. Actualmente hay %.2f %s disponibles y el mínimo recomendado es %.2f %s. Se recomienda realizar reposición del insumo.",
                        product.getName(),
                        product.getQuantity(),
                        product.getUnit(),
                        product.getMinimumQuantity(),
                        product.getUnit()))
                .productId(product.getId())
                .build();
        createAlert(alert);
    }

    private void createExpiryAlert(Product product) {
        AlertNotification alert = AlertNotification.builder()
                .type(DomainValues.AlertType.EXPIRY)
                .title("📅 Vencimiento Próximo: " + product.getName())
                .message(String.format(
                        "El producto %s está próximo a vencer. Fecha de vencimiento: %s. Quedan %d días o menos. Revisa el inventario y prioriza su uso para evitar pérdidas.",
                        product.getName(),
                        product.getExpirationDate(),
                        expiryDays))
                .productId(product.getId())
                .build();
        createAlert(alert);
    }
}
