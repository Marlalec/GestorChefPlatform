package com.gestor.chef.gf.infrastructure.adapter.out.persistence;

import com.gestor.chef.gf.domain.model.Supplier;
import com.gestor.chef.gf.domain.port.out.SupplierRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SupplierRepositoryAdapter implements SupplierRepositoryPort {
    private final SupplierMongoRepository mongoRepository;

    @Override public Supplier save(Supplier s) { return mongoRepository.save(SupplierDocument.fromDomain(s)).toDomain(); }
    @Override public Optional<Supplier> findById(String id) { return mongoRepository.findById(id).map(SupplierDocument::toDomain); }
    @Override public List<Supplier> findAll() { return mongoRepository.findAll().stream().map(SupplierDocument::toDomain).toList(); }
    @Override public Page<Supplier> findAllPaged(Pageable pageable) { return mongoRepository.findAll(pageable).map(SupplierDocument::toDomain); }
    @Override public List<Supplier> findByStatus(String s) { return mongoRepository.findByStatus(s).stream().map(SupplierDocument::toDomain).toList(); }
    @Override public void deleteById(String id) { mongoRepository.deleteById(id); }
}
