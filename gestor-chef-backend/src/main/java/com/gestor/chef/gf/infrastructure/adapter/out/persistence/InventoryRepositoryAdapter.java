package com.gestor.chef.gf.infrastructure.adapter.out.persistence;

import com.gestor.chef.gf.domain.model.Inventory;
import com.gestor.chef.gf.domain.port.out.InventoryRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InventoryRepositoryAdapter implements InventoryRepositoryPort {
    private final InventoryMongoRepository mongoRepository;

    @Override public Inventory save(Inventory i) { return mongoRepository.save(InventoryDocument.fromDomain(i)).toDomain(); }
    @Override public Optional<Inventory> findById(String id) { return mongoRepository.findById(id).map(InventoryDocument::toDomain); }
    @Override public Optional<Inventory> findByProductId(String pid) { return mongoRepository.findByProductId(pid).map(InventoryDocument::toDomain); }
    @Override public List<Inventory> findAll() { return mongoRepository.findAll().stream().map(InventoryDocument::toDomain).toList(); }
    @Override public List<Inventory> findLowStock() { return mongoRepository.findLowStockItems().stream().map(InventoryDocument::toDomain).toList(); }
    @Override public void deleteById(String id) { mongoRepository.deleteById(id); }
}
