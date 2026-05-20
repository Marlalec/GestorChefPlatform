package com.gestor.chef.gf.infrastructure.adapter.out.persistence;

import com.gestor.chef.gf.domain.model.Order;
import com.gestor.chef.gf.domain.port.out.OrderRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepositoryPort {
    private final OrderMongoRepository mongoRepository;

    @Override public Order save(Order o) { return mongoRepository.save(OrderDocument.fromDomain(o)).toDomain(); }
    @Override public Optional<Order> findById(String id) { return mongoRepository.findById(id).map(OrderDocument::toDomain); }
    @Override public List<Order> findAll() { return mongoRepository.findAll().stream().map(OrderDocument::toDomain).toList(); }
    @Override public Page<Order> findAllPaged(Pageable pageable) { return mongoRepository.findAll(pageable).map(OrderDocument::toDomain); }
    @Override public List<Order> findByStatus(String s) { return mongoRepository.findByStatus(s).stream().map(OrderDocument::toDomain).toList(); }
    @Override public List<Order> findByCreatedAtBetween(Instant from, Instant to) { return mongoRepository.findByCreatedAtBetween(from, to).stream().map(OrderDocument::toDomain).toList(); }
    @Override public List<Order> findByTableNumber(String t) { return mongoRepository.findByTableNumber(t).stream().map(OrderDocument::toDomain).toList(); }
    @Override public void deleteById(String id) { mongoRepository.deleteById(id); }
}
