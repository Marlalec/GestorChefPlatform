package com.gestor.chef.gf.application.service;

import com.gestor.chef.gf.application.service.support.EntityFinder;
import com.gestor.chef.gf.domain.model.Inventory;
import com.gestor.chef.gf.domain.model.Product;
import com.gestor.chef.gf.domain.port.in.InventoryUseCase;
import com.gestor.chef.gf.domain.port.in.ProductUseCase;
import com.gestor.chef.gf.domain.port.out.InventoryRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService implements InventoryUseCase {

    private final InventoryRepositoryPort inventoryRepository;
    private final ProductUseCase productUseCase;

    @Override
    public Inventory getInventoryByProduct(String productId) {
        return EntityFinder.required(inventoryRepository.findByProductId(productId), "Inventario para producto", productId);
    }

    @Override
    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    @Override
    public List<Inventory> getLowStockItems() {
        return inventoryRepository.findLowStock();
    }

    @Override
    public Inventory updateInventory(String productId, double quantity) {
        Inventory inventory = EntityFinder.required(inventoryRepository.findByProductId(productId), "Inventario para producto", productId);
        inventory.setCurrentQuantity(quantity);
        inventory.setLowStockAlert(quantity < inventory.getMinimumQuantity());
        inventory.setLastUpdated(Instant.now());
        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory createInventoryEntry(Inventory inventory) {
        inventory.setLowStockAlert(inventory.getCurrentQuantity() < inventory.getMinimumQuantity());
        inventory.setLastUpdated(Instant.now());
        return inventoryRepository.save(inventory);
    }

    @Override
    public void syncFromProducts() {
        productUseCase.getAllProducts().forEach(this::syncProduct);
    }

    private void syncProduct(Product product) {
        inventoryRepository.findByProductId(product.getId()).ifPresentOrElse(
                inventory -> updateExistingInventory(inventory, product),
                () -> createInventoryFromProduct(product)
        );
    }

    private void updateExistingInventory(Inventory inventory, Product product) {
        inventory.setCurrentQuantity(product.getQuantity());
        inventory.setMinimumQuantity(product.getMinimumQuantity());
        inventory.setLowStockAlert(product.isLowStock());
        inventory.setLastUpdated(Instant.now());
        inventoryRepository.save(inventory);
    }

    private void createInventoryFromProduct(Product product) {
        Inventory newEntry = Inventory.builder()
                .productId(product.getId())
                .productName(product.getName())
                .category(product.getCategory())
                .currentQuantity(product.getQuantity())
                .minimumQuantity(product.getMinimumQuantity())
                .unit(product.getUnit())
                .lowStockAlert(product.isLowStock())
                .lastUpdated(Instant.now())
                .build();
        inventoryRepository.save(newEntry);
    }
}
