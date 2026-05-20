package com.gestor.chef.gf.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {

    private List<Product> criticalProducts;
    private int criticalProductsCount;

    private List<Product> expiringProducts;
    private int expiringProductsCount;

    private int recentPurchasesCount;
    private BigDecimal recentPurchasesValue;

    private int weeklyConsumptionMovements;
    private BigDecimal weeklyConsumptionValue;

    private int monthlyWasteMovements;
    private BigDecimal estimatedLossesValue;

    private int pendingOrdersCount;
    private int inProgressOrdersCount;
    private BigDecimal todayRevenueEstimate;

    private int unreadAlertsCount;

    private int totalActiveProducts;
    private int totalActiveSuppliers;

    private List<InventoryMovement> recentMovements;
}
