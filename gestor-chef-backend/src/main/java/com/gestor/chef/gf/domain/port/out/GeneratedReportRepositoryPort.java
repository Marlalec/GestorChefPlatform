package com.gestor.chef.gf.domain.port.out;

import com.gestor.chef.gf.domain.model.GeneratedReport;
import java.util.List;
import java.util.Optional;

public interface GeneratedReportRepositoryPort {
    GeneratedReport save(GeneratedReport report);
    Optional<GeneratedReport> findById(String id);
    List<GeneratedReport> findAll();
    List<GeneratedReport> findByType(String type);
    List<GeneratedReport> findByGeneratedBy(String userId);
    void deleteById(String id);
}
