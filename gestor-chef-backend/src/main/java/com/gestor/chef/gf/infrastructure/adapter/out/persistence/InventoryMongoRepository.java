package com.gestor.chef.gf.infrastructure.adapter.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryMongoRepository extends MongoRepository<InventoryDocument, String> {
    Optional<InventoryDocument> findByProductId(String productId);

    @Query("{ 'lowStockAlert': true }")
    List<InventoryDocument> findLowStockItems();
}
