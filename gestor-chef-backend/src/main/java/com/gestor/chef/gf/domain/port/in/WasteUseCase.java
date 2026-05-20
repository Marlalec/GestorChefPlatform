package com.gestor.chef.gf.domain.port.in;

import com.gestor.chef.gf.domain.model.Waste;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface WasteUseCase {

    Waste registerWaste(Waste waste);

    Optional<Waste> getWasteById(String id);

    List<Waste> getAllWastes();

    Page<Waste> getAllWastesPaged(Pageable pageable);

    List<Waste> getWastesByProduct(String productId);

    List<Waste> getWastesByCause(String cause);

    List<Waste> getWastesByUser(String userId);

    List<Waste> getWastesByDateRange(Instant from, Instant to);
}
