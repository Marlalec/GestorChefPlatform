package com.gestor.chef.gf.infrastructure.adapter.out.persistence;

import com.gestor.chef.gf.domain.model.InventoryMovement;
import com.gestor.chef.gf.domain.port.out.InventoryMovementRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InventoryMovementRepositoryAdapter implements InventoryMovementRepositoryPort {

    private final InventoryMovementMongoRepository mongoRepository;

    @Override public InventoryMovement save(InventoryMovement m) { return mongoRepository.save(InventoryMovementDocument.fromDomain(m)).toDomain(); }
    @Override public Optional<InventoryMovement> findById(String id) { return mongoRepository.findById(id).map(InventoryMovementDocument::toDomain); }
    @Override public List<InventoryMovement> findAll() { return mongoRepository.findAll().stream().map(InventoryMovementDocument::toDomain).toList(); }
    @Override public Page<InventoryMovement> findAllPaged(Pageable pageable) { return mongoRepository.findAll(pageable).map(InventoryMovementDocument::toDomain); }
    @Override public List<InventoryMovement> findByProductId(String pid) { return mongoRepository.findByProductId(pid).stream().map(InventoryMovementDocument::toDomain).toList(); }
    @Override public List<InventoryMovement> findByMovementType(String type) { return mongoRepository.findByMovementType(type).stream().map(InventoryMovementDocument::toDomain).toList(); }
    @Override public List<InventoryMovement> findByTimestampBetween(Instant from, Instant to) { return mongoRepository.findByTimestampBetween(from, to).stream().map(InventoryMovementDocument::toDomain).toList(); }
    @Override public List<InventoryMovement> findByUserId(String uid) { return mongoRepository.findByUserId(uid).stream().map(InventoryMovementDocument::toDomain).toList(); }
}
