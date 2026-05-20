package com.gestor.chef.gf.domain.port.out;

import com.gestor.chef.gf.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProductRepositoryPort {
    Product save(Product product);
    Optional<Product> findById(String id);
    List<Product> findAll();
    Page<Product> findAllPaged(Pageable pageable);
    List<Product> findByStatus(String status);
    List<Product> findByCategory(String category);
    List<Product> findByCategoryAndStatus(String category, String status);
    List<Product> findBySupplierId(String supplierId);
    List<Product> findBySupplierIdAndStatus(String supplierId, String status);
    List<Product> findLowStockProducts();
    List<Product> findByExpirationDateBefore(LocalDate date);
    void deleteById(String id);
    boolean existsById(String id);
}
