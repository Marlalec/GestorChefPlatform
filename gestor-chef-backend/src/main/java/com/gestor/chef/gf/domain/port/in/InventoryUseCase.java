package com.gestor.chef.gf.domain.port.in;

import com.gestor.chef.gf.domain.model.Inventory;
import java.util.List;

public interface InventoryUseCase {
    Inventory getInventoryByProduct(String productId);
    List<Inventory> getAllInventory();
    List<Inventory> getLowStockItems();
    Inventory updateInventory(String productId, double quantity);
    Inventory createInventoryEntry(Inventory inventory);
    void syncFromProducts();
}
