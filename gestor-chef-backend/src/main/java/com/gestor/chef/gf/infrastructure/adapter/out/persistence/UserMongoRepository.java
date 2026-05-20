package com.gestor.chef.gf.infrastructure.adapter.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserMongoRepository extends MongoRepository<UserDocument, String> {
    Optional<UserDocument> findByEmail(String email);
    List<UserDocument> findByRol(String rol);
    List<UserDocument> findByAccountStatus(String accountStatus);
    boolean existsByEmail(String email);
}
