package com.gestor.chef.gf.application.service;

import com.gestor.chef.gf.domain.model.*;
import com.gestor.chef.gf.domain.port.in.*;
import com.gestor.chef.gf.domain.port.out.AlertNotificationRepositoryPort;
import com.gestor.chef.gf.domain.port.out.InventoryMovementRepositoryPort;
import com.gestor.chef.gf.domain.port.out.OrderRepositoryPort;
import com.gestor.chef.gf.domain.port.out.SupplierRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService implements DashboardUseCase {

    private final ProductUseCase productUseCase;
    private final AlertNotificationRepositoryPort alertRepository;
    private final InventoryMovementRepositoryPort movementRepository;
    private final OrderRepositoryPort orderRepository;
    private final SupplierRepositoryPort supplierRepository;

    @Override
    public DashboardStats getDashboardStats() {
        log.debug("[Dashboard] Calculando indicadores...");

        Instant now      = Instant.now();
        Instant minus7d  = now.minus(7,  ChronoUnit.DAYS);
        Instant minus30d = now.minus(30, ChronoUnit.DAYS);

        List<Product> critical = safeGet(() -> productUseCase.getLowStockProducts());

        List<Product> expiring = safeGet(() ->
                productUseCase.getExpiringProducts(LocalDate.now().plusDays(7)));

        List<InventoryMovement> week7 = safeGet(() ->
                movementRepository.findByTimestampBetween(minus7d, now));

        List<InventoryMovement> purchasesWeek = week7.stream()
                .filter(m -> "IN".equals(m.getMovementType()))
                .toList();

        BigDecimal purchasesValue = purchasesWeek.stream()
                .map(m -> priceOf(m.getProductId()).multiply(BigDecimal.valueOf(m.getQuantityChanged())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<InventoryMovement> outWeek = week7.stream()
                .filter(m -> "OUT".equals(m.getMovementType()))
                .toList();

        BigDecimal consumptionValue = outWeek.stream()
                .map(m -> priceOf(m.getProductId()).multiply(BigDecimal.valueOf(m.getQuantityChanged())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<InventoryMovement> waste30 = safeGet(() ->
                movementRepository.findByTimestampBetween(minus30d, now)).stream()
                .filter(m -> "WASTE".equals(m.getMovementType()))
                .toList();

        BigDecimal lossesValue = waste30.stream()
                .map(m -> priceOf(m.getProductId()).multiply(BigDecimal.valueOf(m.getQuantityChanged())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Order> pending    = safeGet(() -> orderRepository.findByStatus("PENDING"));
        List<Order> inProgress = safeGet(() -> orderRepository.findByStatus("IN_PROGRESS"));

        BigDecimal todayRevenue = safeGet(() -> orderRepository
                .findByCreatedAtBetween(Instant.now().truncatedTo(ChronoUnit.DAYS), now))
                .stream()
                .filter(o -> "COMPLETED".equals(o.getStatus()))
                .map(o -> o.getTotalAmount() != null ? o.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long unreadCount = safeGet(() -> alertRepository.findAll()).stream()
                .filter(a -> "UNREAD".equals(a.getStatus()))
                .count();

        int totalProducts  = safeGet(() -> productUseCase.getAllProducts()).size();
        int totalSuppliers = safeGet(() -> supplierRepository.findByStatus("ACTIVE")).size();

        List<InventoryMovement> recent = safeGet(() ->
                movementRepository.findByTimestampBetween(minus7d, now)).stream()
                .sorted(Comparator.comparing(InventoryMovement::getTimestamp).reversed())
                .limit(10)
                .toList();

        log.debug("[Dashboard] Indicadores calculados correctamente.");

        return DashboardStats.builder()

                .criticalProducts(critical)
                .criticalProductsCount(critical.size())

                .expiringProducts(expiring)
                .expiringProductsCount(expiring.size())

                .recentPurchasesCount(purchasesWeek.size())
                .recentPurchasesValue(purchasesValue)

                .weeklyConsumptionMovements(outWeek.size())
                .weeklyConsumptionValue(consumptionValue)

                .monthlyWasteMovements(waste30.size())
                .estimatedLossesValue(lossesValue)

                .pendingOrdersCount(pending.size())
                .inProgressOrdersCount(inProgress.size())
                .todayRevenueEstimate(todayRevenue)

                .unreadAlertsCount((int) unreadCount)

                .totalActiveProducts(totalProducts)
                .totalActiveSuppliers(totalSuppliers)

                .recentMovements(recent)
                .build();
    }

    private BigDecimal priceOf(String productId) {
        if (productId == null) return BigDecimal.ZERO;
        try {
            return productUseCase.getProductById(productId)
                    .map(p -> p.getPrice() != null ? p.getPrice() : BigDecimal.ZERO)
                    .orElse(BigDecimal.ZERO);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private <T> List<T> safeGet(java.util.function.Supplier<List<T>> supplier) {
        try {
            List<T> result = supplier.get();
            return result != null ? result : List.of();
        } catch (Exception e) {
            log.warn("[Dashboard] Error obteniendo datos: {}", e.getMessage());
            return List.of();
        }
    }
}
