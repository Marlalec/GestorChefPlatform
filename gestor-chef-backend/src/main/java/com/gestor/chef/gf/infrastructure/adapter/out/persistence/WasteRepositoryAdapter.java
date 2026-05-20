package com.gestor.chef.gf.infrastructure.adapter.out.persistence;

import com.gestor.chef.gf.domain.model.Waste;
import com.gestor.chef.gf.domain.port.out.WasteRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WasteRepositoryAdapter implements WasteRepositoryPort {

    private final WasteMongoRepository mongoRepository;

    @Override public Waste save(Waste w)                             { return mongoRepository.save(WasteDocument.fromDomain(w)).toDomain(); }
    @Override public Optional<Waste> findById(String id)            { return mongoRepository.findById(id).map(WasteDocument::toDomain); }
    @Override public List<Waste> findAll()                          { return mongoRepository.findAll().stream().map(WasteDocument::toDomain).toList(); }
    @Override public Page<Waste> findAllPaged(Pageable p)           { return mongoRepository.findAll(p).map(WasteDocument::toDomain); }
    @Override public List<Waste> findByProductId(String pid)        { return mongoRepository.findByProductId(pid).stream().map(WasteDocument::toDomain).toList(); }
    @Override public List<Waste> findByCause(String cause)          { return mongoRepository.findByCause(cause).stream().map(WasteDocument::toDomain).toList(); }
    @Override public List<Waste> findByReportedBy(String uid)       { return mongoRepository.findByReportedBy(uid).stream().map(WasteDocument::toDomain).toList(); }
    @Override public List<Waste> findByOccurredAtBetween(Instant f, Instant t) {
        return mongoRepository.findByOccurredAtBetween(f, t).stream().map(WasteDocument::toDomain).toList();
    }
}
