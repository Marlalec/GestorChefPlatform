package com.gestor.chef.gf.infrastructure.adapter.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GeneratedReportMongoRepository extends MongoRepository<GeneratedReportDocument, String> {
    List<GeneratedReportDocument> findByType(String type);
    List<GeneratedReportDocument> findByGeneratedBy(String userId);
}
