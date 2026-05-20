package com.gestor.chef.gf.infrastructure.adapter.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface WasteMongoRepository extends MongoRepository<WasteDocument, String> {
    List<WasteDocument> findByProductId(String productId);
    List<WasteDocument> findByCause(String cause);
    List<WasteDocument> findByReportedBy(String userId);
    List<WasteDocument> findByOccurredAtBetween(Instant from, Instant to);
}
