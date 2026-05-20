package com.gestor.chef.gf.infrastructure.adapter.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleMongoRepository extends MongoRepository<RoleDocument, String> {
    Optional<RoleDocument> findByName(String name);
}
