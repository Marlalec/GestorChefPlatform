package com.gestor.chef.gf.infrastructure.adapter.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlertNotificationMongoRepository extends MongoRepository<AlertNotificationDocument, String> {

    @Query("{ $or: [ { 'targetUserId': ?0 }, { 'targetUserId': null } ] }")
    List<AlertNotificationDocument> findByTargetUserIdOrBroadcast(String userId);

    @Query("{ 'status': ?0, $or: [ { 'targetUserId': ?1 }, { 'targetUserId': null } ] }")
    List<AlertNotificationDocument> findByStatusAndUserId(String status, String userId);

    List<AlertNotificationDocument> findByType(String type);
}
