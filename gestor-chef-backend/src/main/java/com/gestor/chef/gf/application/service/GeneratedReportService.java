package com.gestor.chef.gf.application.service;

import com.gestor.chef.gf.domain.model.GeneratedReport;
import com.gestor.chef.gf.domain.model.InventoryMovement;
import com.gestor.chef.gf.domain.model.Order;
import com.gestor.chef.gf.domain.model.Product;
import com.gestor.chef.gf.domain.model.value.DomainValues;
import com.gestor.chef.gf.domain.port.in.GeneratedReportUseCase;
import com.gestor.chef.gf.domain.port.in.InventoryMovementUseCase;
import com.gestor.chef.gf.domain.port.in.OrderUseCase;
import com.gestor.chef.gf.domain.port.in.ProductUseCase;
import com.gestor.chef.gf.domain.port.out.GeneratedReportRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GeneratedReportService implements GeneratedReportUseCase {

    private final GeneratedReportRepositoryPort reportRepository;
    private final ProductUseCase productUseCase;
    private final InventoryMovementUseCase movementUseCase;
    private final OrderUseCase orderUseCase;

    @Override
    public GeneratedReport generateInventoryReport(String userId, String periodStart, String periodEnd) {
        List<Product> products = productUseCase.getAllProducts();
        List<Product> lowStock = productUseCase.getLowStockProducts();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("totalProductos", products.size());
        data.put("stockBajo", lowStock.size());
        data.put("totalUnidades", products.stream().mapToDouble(Product::getQuantity).sum());
        data.put("valorTotalInventario", products.stream().mapToDouble(this::inventoryValue).sum());
        data.put("categorias", countProductsByCategory(products));
        data.put("productosStockBajo", lowStock.stream()
                .map(product -> Map.of("sku", product.getId(), "nombre", product.getName(), "stock", product.getQuantity(), "minimo", product.getMinimumQuantity()))
                .collect(Collectors.toList()));
        return saveReport(DomainValues.ReportType.INVENTORY, "Reporte de Inventario", data, userId, periodStart, periodEnd);
    }

    @Override
    public GeneratedReport generateFinancialReport(String userId, String periodStart, String periodEnd) {
        List<Order> paidOrders = orderUseCase.getOrdersByStatus(DomainValues.Status.COMPLETED);
        List<Product> products = productUseCase.getAllProducts();
        double totalSales = paidOrders.stream().mapToDouble(this::orderTotal).sum();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("totalVentas", totalSales);
        data.put("totalOrdenes", paidOrders.size());
        data.put("ticketPromedio", paidOrders.isEmpty() ? 0 : totalSales / paidOrders.size());
        data.put("valorInventario", products.stream().mapToDouble(this::inventoryValue).sum());
        data.put("desglosePorCategoria", products.stream()
                .collect(Collectors.groupingBy(this::categoryOrDefault, Collectors.summingDouble(this::inventoryValue))));
        return saveReport(DomainValues.ReportType.FINANCIAL, "Reporte Financiero", data, userId, periodStart, periodEnd);
    }

    @Override
    public GeneratedReport generateWasteReport(String userId, String periodStart, String periodEnd) {
        List<InventoryMovement> wasteMovements = movementUseCase.getMovementsByType(DomainValues.MovementType.WASTE);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("totalMovimientosMerma", wasteMovements.size());
        data.put("totalCantidadMerma", wasteMovements.stream().mapToDouble(InventoryMovement::getQuantityChanged).sum());
        data.put("mermaPorProducto", wasteMovements.stream()
                .collect(Collectors.groupingBy(this::movementProductLabel, Collectors.summingDouble(InventoryMovement::getQuantityChanged))));
        data.put("mermaPorUsuario", wasteMovements.stream()
                .collect(Collectors.groupingBy(this::movementUserLabel, Collectors.counting())));
        return saveReport(DomainValues.ReportType.WASTE, "Reporte de Desperdicios y Mermas", data, userId, periodStart, periodEnd);
    }

    @Override
    public GeneratedReport generateDemandProjection(String userId) {
        List<InventoryMovement> outMovements = movementUseCase.getMovementsByType(DomainValues.MovementType.OUT);
        List<InventoryMovement> recipeUse = outMovements.stream()
                .filter(movement -> DomainValues.MovementReason.RECIPE_USE.equals(movement.getReason()))
                .toList();
        Map<String, Double> productConsumption = recipeUse.stream()
                .collect(Collectors.groupingBy(this::movementProductLabel, Collectors.summingDouble(InventoryMovement::getQuantityChanged)));
        List<Map<String, Object>> topConsumption = productConsumption.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(10)
                .map(entry -> Map.of(
                        "producto", (Object) entry.getKey(),
                        "consumoTotal", (Object) entry.getValue(),
                        "proyeccionSemanal", (Object) (entry.getValue() / Math.max(1, outMovements.size()) * 7)))
                .collect(Collectors.toList());
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("topProductosMasConsumidos", topConsumption);
        data.put("totalMovimientosAnalizados", recipeUse.size());
        data.put("proyeccionSemanal", productConsumption.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue() / Math.max(1, recipeUse.size()) * 7)));
        return reportRepository.save(GeneratedReport.builder()
                .type(DomainValues.ReportType.DEMAND)
                .title("Proyección de Demanda Semanal (Base IA)")
                .data(data)
                .generatedAt(Instant.now())
                .generatedBy(userId)
                .format(DomainValues.ReportType.JSON)
                .build());
    }

    @Override
    public void deleteReport(String id) {
        reportRepository.deleteById(id);
    }

    @Override
    public Optional<GeneratedReport> getReportById(String id) {
        return reportRepository.findById(id);
    }

    @Override
    public List<GeneratedReport> getAllReports() {
        return reportRepository.findAll();
    }

    @Override
    public List<GeneratedReport> getReportsByType(String type) {
        return reportRepository.findByType(type);
    }

    @Override
    public List<GeneratedReport> getReportsByUser(String userId) {
        return reportRepository.findByGeneratedBy(userId);
    }

    private GeneratedReport saveReport(String type, String title, Map<String, Object> data, String userId, String periodStart, String periodEnd) {
        return reportRepository.save(GeneratedReport.builder()
                .type(type)
                .title(title)
                .data(data)
                .generatedAt(Instant.now())
                .generatedBy(userId)
                .periodStart(periodStart)
                .periodEnd(periodEnd)
                .format(DomainValues.ReportType.JSON)
                .build());
    }

    private Map<String, Long> countProductsByCategory(List<Product> products) {
        return products.stream().collect(Collectors.groupingBy(this::categoryOrDefault, Collectors.counting()));
    }

    private double inventoryValue(Product product) {
        return product.getQuantity() * (product.getPrice() != null ? product.getPrice().doubleValue() : 0);
    }

    private double orderTotal(Order order) {
        return order.getTotalAmount() != null ? order.getTotalAmount().doubleValue() : 0;
    }

    private String categoryOrDefault(Product product) {
        return product.getCategory() != null ? product.getCategory() : "SIN CATEGORÍA";
    }

    private String movementProductLabel(InventoryMovement movement) {
        return movement.getProductName() != null ? movement.getProductName() : movement.getProductId();
    }

    private String movementUserLabel(InventoryMovement movement) {
        return movement.getUserId() != null ? movement.getUserId() : "desconocido";
    }
}
