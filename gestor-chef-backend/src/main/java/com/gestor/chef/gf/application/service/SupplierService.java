package com.gestor.chef.gf.application.service;

import com.gestor.chef.gf.application.service.support.EntityFinder;
import com.gestor.chef.gf.domain.model.Supplier;
import com.gestor.chef.gf.domain.model.value.DomainValues;
import com.gestor.chef.gf.domain.port.in.SupplierUseCase;
import com.gestor.chef.gf.domain.port.out.SupplierRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.gestor.chef.gf.application.service.support.DomainValidator.requireAllowed;

@Service
@RequiredArgsConstructor
public class SupplierService implements SupplierUseCase {

    private final SupplierRepositoryPort supplierRepository;

    @Override
    @CacheEvict(value = {"suppliers", "suppliers_active"}, allEntries = true)
    public Supplier createSupplier(Supplier supplier) {
        Instant now = Instant.now();
        supplier.setStatus(DomainValues.Status.ACTIVE);
        supplier.setCreatedAt(now);
        supplier.setUpdatedAt(now);
        return supplierRepository.save(supplier);
    }

    @Override
    @CacheEvict(value = {"suppliers", "suppliers_active"}, allEntries = true)
    public Supplier updateSupplier(String id, Supplier supplier) {
        Supplier existing = EntityFinder.required(supplierRepository.findById(id), "Proveedor", id);
        mergeSupplier(existing, supplier);
        existing.setUpdatedAt(Instant.now());
        return supplierRepository.save(existing);
    }

    @Override
    @Cacheable(value = "suppliers", key = "#id")
    public Optional<Supplier> getSupplierById(String id) {
        return supplierRepository.findById(id);
    }

    @Override
    @Cacheable(value = "suppliers", unless = "#result.isEmpty()")
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findByStatus(DomainValues.Status.ACTIVE);
    }

    @Override
    public Page<Supplier> getAllSuppliersPaged(Pageable pageable) {
        return supplierRepository.findAllPaged(pageable);
    }

    @Override
    @Cacheable(value = "suppliers_active", unless = "#result.isEmpty()")
    public List<Supplier> getActiveSuppliers() {
        return supplierRepository.findByStatus(DomainValues.Status.ACTIVE);
    }

    @Override
    @CacheEvict(value = {"suppliers", "suppliers_active"}, allEntries = true)
    public void deleteSupplier(String id) {
        Supplier supplier = EntityFinder.required(supplierRepository.findById(id), "Proveedor", id);
        supplier.setStatus(DomainValues.Status.INACTIVE);
        supplier.setUpdatedAt(Instant.now());
        supplierRepository.save(supplier);
    }

    @Override
    @CacheEvict(value = {"suppliers", "suppliers_active"}, allEntries = true)
    public Supplier changeStatus(String id, String status) {
        Supplier supplier = EntityFinder.required(supplierRepository.findById(id), "Proveedor", id);
        supplier.setStatus(requireAllowed(status, DomainValues.Status.ACCOUNT, "status"));
        supplier.setUpdatedAt(Instant.now());
        return supplierRepository.save(supplier);
    }

    private void mergeSupplier(Supplier existing, Supplier update) {
        if (update.getName() != null) existing.setName(update.getName());
        if (update.getContactName() != null) existing.setContactName(update.getContactName());
        if (update.getEmail() != null) existing.setEmail(update.getEmail());
        if (update.getPhone() != null) existing.setPhone(update.getPhone());
        if (update.getAddress() != null) existing.setAddress(update.getAddress());
        if (update.getProductIds() != null) existing.setProductIds(update.getProductIds());
    }
}
