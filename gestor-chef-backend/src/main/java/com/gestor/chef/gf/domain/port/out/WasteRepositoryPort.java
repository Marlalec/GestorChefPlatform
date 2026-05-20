package com.gestor.chef.gf.domain.port.out;

import com.gestor.chef.gf.domain.model.Waste;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface WasteRepositoryPort {
    Waste save(Waste waste);
    Optional<Waste> findById(String id);
    List<Waste> findAll();
    Page<Waste> findAllPaged(Pageable pageable);
    List<Waste> findByProductId(String productId);
    List<Waste> findByCause(String cause);
    List<Waste> findByReportedBy(String userId);
    List<Waste> findByOccurredAtBetween(Instant from, Instant to);
}
