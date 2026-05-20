package com.gestor.chef.gf.infrastructure.adapter.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;

@Repository
public interface InventoryMovementMongoRepository extends MongoRepository<InventoryMovementDocument, String> {
    List<InventoryMovementDocument> findByProductId(String productId);
    List<InventoryMovementDocument> findByMovementType(String movementType);
    List<InventoryMovementDocument> findByTimestampBetween(Instant from, Instant to);
    List<InventoryMovementDocument> findByUserId(String userId);
}
