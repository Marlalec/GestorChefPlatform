package com.gestor.chef.gf.infrastructure.adapter.out.persistence;

import com.gestor.chef.gf.domain.model.Product;
import com.gestor.chef.gf.domain.port.out.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepositoryPort {

    private final ProductMongoRepository mongoRepository;

    @Override public Product save(Product p) { return mongoRepository.save(ProductDocument.fromDomain(p)).toDomain(); }
    @Override public Optional<Product> findById(String id) { return mongoRepository.findById(id).map(ProductDocument::toDomain); }
    @Override public List<Product> findAll() { return mongoRepository.findAll().stream().map(ProductDocument::toDomain).toList(); }
    @Override public Page<Product> findAllPaged(Pageable pageable) { return mongoRepository.findAll(pageable).map(ProductDocument::toDomain); }
    @Override public List<Product> findByStatus(String status) { return mongoRepository.findByStatus(status).stream().map(ProductDocument::toDomain).toList(); }
    @Override public List<Product> findByCategory(String c) { return mongoRepository.findByCategory(c).stream().map(ProductDocument::toDomain).toList(); }
    @Override public List<Product> findByCategoryAndStatus(String c, String s) { return mongoRepository.findByCategoryAndStatus(c, s).stream().map(ProductDocument::toDomain).toList(); }
    @Override public List<Product> findBySupplierId(String sid) { return mongoRepository.findBySupplierId(sid).stream().map(ProductDocument::toDomain).toList(); }
    @Override public List<Product> findBySupplierIdAndStatus(String sid, String s) { return mongoRepository.findBySupplierIdAndStatus(sid, s).stream().map(ProductDocument::toDomain).toList(); }
    @Override public List<Product> findLowStockProducts() { return mongoRepository.findLowStockProducts().stream().map(ProductDocument::toDomain).toList(); }
    @Override public List<Product> findByExpirationDateBefore(LocalDate date) { return mongoRepository.findByExpirationDateBefore(date).stream().map(ProductDocument::toDomain).toList(); }
    @Override public void deleteById(String id) { mongoRepository.deleteById(id); }
    @Override public boolean existsById(String id) { return mongoRepository.existsById(id); }
}
