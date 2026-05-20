package com.gestor.chef.gf.domain.port.out;

import com.gestor.chef.gf.domain.model.InventoryMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface InventoryMovementRepositoryPort {
    InventoryMovement save(InventoryMovement movement);
    Optional<InventoryMovement> findById(String id);
    List<InventoryMovement> findAll();
    Page<InventoryMovement> findAllPaged(Pageable pageable);
    List<InventoryMovement> findByProductId(String productId);
    List<InventoryMovement> findByMovementType(String movementType);
    List<InventoryMovement> findByTimestampBetween(Instant from, Instant to);
    List<InventoryMovement> findByUserId(String userId);
}
