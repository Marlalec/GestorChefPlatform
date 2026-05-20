package com.gestor.chef.gf.infrastructure.adapter.out.persistence;

import com.gestor.chef.gf.domain.model.GeneratedReport;
import com.gestor.chef.gf.domain.port.out.GeneratedReportRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GeneratedReportRepositoryAdapter implements GeneratedReportRepositoryPort {
    private final GeneratedReportMongoRepository mongoRepository;

    @Override public GeneratedReport save(GeneratedReport r) { return mongoRepository.save(GeneratedReportDocument.fromDomain(r)).toDomain(); }
    @Override public Optional<GeneratedReport> findById(String id) { return mongoRepository.findById(id).map(GeneratedReportDocument::toDomain); }
    @Override public List<GeneratedReport> findAll() { return mongoRepository.findAll().stream().map(GeneratedReportDocument::toDomain).toList(); }
    @Override public List<GeneratedReport> findByType(String t) { return mongoRepository.findByType(t).stream().map(GeneratedReportDocument::toDomain).toList(); }
    @Override public List<GeneratedReport> findByGeneratedBy(String uid) { return mongoRepository.findByGeneratedBy(uid).stream().map(GeneratedReportDocument::toDomain).toList(); }
    @Override public void deleteById(String id) { mongoRepository.deleteById(id); }
}
