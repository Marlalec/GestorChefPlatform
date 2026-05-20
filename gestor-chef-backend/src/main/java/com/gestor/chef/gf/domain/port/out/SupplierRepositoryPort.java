package com.gestor.chef.gf.domain.port.out;

import com.gestor.chef.gf.domain.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface SupplierRepositoryPort {
    Supplier save(Supplier supplier);
    Optional<Supplier> findById(String id);
    List<Supplier> findAll();
    Page<Supplier> findAllPaged(Pageable pageable);
    List<Supplier> findByStatus(String status);
    void deleteById(String id);
}
