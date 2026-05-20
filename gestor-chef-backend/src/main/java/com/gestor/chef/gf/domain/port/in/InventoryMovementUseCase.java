package com.gestor.chef.gf.domain.port.in;

import com.gestor.chef.gf.domain.model.InventoryMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface InventoryMovementUseCase {
    InventoryMovement registerMovement(InventoryMovement movement);
    Optional<InventoryMovement> getMovementById(String id);
    List<InventoryMovement> getAllMovements();
    Page<InventoryMovement> getAllMovementsPaged(Pageable pageable);
    List<InventoryMovement> getMovementsByProduct(String productId);
    List<InventoryMovement> getMovementsByType(String movementType);
    List<InventoryMovement> getMovementsByDateRange(Instant from, Instant to);
    List<InventoryMovement> getMovementsByUser(String userId);
    InventoryMovement registerScaleWeight(String productId, double weight, String userId);
}
