package com.gestor.chef.gf.infrastructure.adapter.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProductMongoRepository extends MongoRepository<ProductDocument, String> {
    List<ProductDocument> findByCategory(String category);
    List<ProductDocument> findBySupplierId(String supplierId);
    List<ProductDocument> findByExpirationDateBefore(LocalDate date);

    List<ProductDocument> findByStatus(String status);
    List<ProductDocument> findByCategoryAndStatus(String category, String status);
    List<ProductDocument> findBySupplierIdAndStatus(String supplierId, String status);

    @Query("{ $expr: { $lt: ['$quantity', '$minimumQuantity'] } }")
    List<ProductDocument> findLowStockProducts();
}
