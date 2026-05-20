package com.gestor.chef.gf.domain.port.out;

import com.gestor.chef.gf.domain.model.Inventory;
import java.util.List;
import java.util.Optional;

public interface InventoryRepositoryPort {
    Inventory save(Inventory inventory);
    Optional<Inventory> findById(String id);
    Optional<Inventory> findByProductId(String productId);
    List<Inventory> findAll();
    List<Inventory> findLowStock();
    void deleteById(String id);
}
