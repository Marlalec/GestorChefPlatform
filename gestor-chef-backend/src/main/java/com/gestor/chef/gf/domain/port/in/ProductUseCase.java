package com.gestor.chef.gf.domain.port.in;

import com.gestor.chef.gf.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProductUseCase {
    Product createProduct(Product product);
    Product updateProduct(String id, Product product);
    Optional<Product> getProductById(String id);
    List<Product> getAllProducts();
    Page<Product> getAllProductsPaged(Pageable pageable);
    List<Product> getProductsByCategory(String category);
    List<Product> getProductsBySupplier(String supplierId);
    List<Product> getLowStockProducts();
    List<Product> getExpiringProducts(LocalDate before);
    void deleteProduct(String id);
    Product updateStock(String id, double newQuantity);
}
