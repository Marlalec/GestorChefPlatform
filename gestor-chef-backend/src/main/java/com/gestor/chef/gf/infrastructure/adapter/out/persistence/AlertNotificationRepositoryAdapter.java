package com.gestor.chef.gf.infrastructure.adapter.out.persistence;

import com.gestor.chef.gf.domain.model.AlertNotification;
import com.gestor.chef.gf.domain.port.out.AlertNotificationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AlertNotificationRepositoryAdapter implements AlertNotificationRepositoryPort {

    private final AlertNotificationMongoRepository mongoRepository;
    private final MongoTemplate mongoTemplate;

    @Override public AlertNotification save(AlertNotification a) { return mongoRepository.save(AlertNotificationDocument.fromDomain(a)).toDomain(); }
    @Override public Optional<AlertNotification> findById(String id) { return mongoRepository.findById(id).map(AlertNotificationDocument::toDomain); }
    @Override public List<AlertNotification> findAll() { return mongoRepository.findAll().stream().map(AlertNotificationDocument::toDomain).toList(); }
    @Override public List<AlertNotification> findByTargetUserIdOrBroadcast(String uid) { return mongoRepository.findByTargetUserIdOrBroadcast(uid).stream().map(AlertNotificationDocument::toDomain).toList(); }
    @Override public List<AlertNotification> findByStatusAndUserId(String st, String uid) { return mongoRepository.findByStatusAndUserId(st, uid).stream().map(AlertNotificationDocument::toDomain).toList(); }
    @Override public List<AlertNotification> findByType(String type) { return mongoRepository.findByType(type).stream().map(AlertNotificationDocument::toDomain).toList(); }
    @Override public void deleteById(String id) { mongoRepository.deleteById(id); }

    @Override
    public void markAllAsReadByUserId(String userId) {
        Query query = new Query(
                new Criteria().andOperator(
                        Criteria.where("status").is("UNREAD"),
                        new Criteria().orOperator(
                                Criteria.where("targetUserId").is(userId),
                                Criteria.where("targetUserId").is(null)
                        )
                )
        );
        Update update = new Update()
                .set("status", "READ")
                .set("readAt", Instant.now());
        mongoTemplate.updateMulti(query, update, AlertNotificationDocument.class);
    }

    @Override
    public boolean existsOpenAlert(String productId, String type) {
        Query query = new Query(
                new Criteria().andOperator(
                        Criteria.where("productId").is(productId),
                        Criteria.where("type").is(type)
                )
        );

        return mongoTemplate.exists(query, AlertNotificationDocument.class);
    }
}
