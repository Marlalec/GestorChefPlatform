package com.gestor.chef.gf.domain.port.in;

import com.gestor.chef.gf.domain.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface SupplierUseCase {
    Supplier createSupplier(Supplier supplier);
    Supplier updateSupplier(String id, Supplier supplier);
    Optional<Supplier> getSupplierById(String id);
    List<Supplier> getAllSuppliers();
    Page<Supplier> getAllSuppliersPaged(Pageable pageable);
    List<Supplier> getActiveSuppliers();
    void deleteSupplier(String id);
    Supplier changeStatus(String id, String status);
}
