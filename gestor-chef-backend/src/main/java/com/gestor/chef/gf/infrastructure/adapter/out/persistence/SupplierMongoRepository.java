package com.gestor.chef.gf.infrastructure.adapter.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SupplierMongoRepository extends MongoRepository<SupplierDocument, String> {
    List<SupplierDocument> findByStatus(String status);
}
