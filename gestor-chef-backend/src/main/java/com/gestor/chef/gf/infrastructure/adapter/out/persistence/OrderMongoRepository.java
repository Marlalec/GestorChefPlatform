package com.gestor.chef.gf.infrastructure.adapter.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;

@Repository
public interface OrderMongoRepository extends MongoRepository<OrderDocument, String> {
    List<OrderDocument> findByStatus(String status);
    List<OrderDocument> findByCreatedAtBetween(Instant from, Instant to);
    List<OrderDocument> findByTableNumber(String tableNumber);
    List<OrderDocument> findByUserId(String userId);
}
