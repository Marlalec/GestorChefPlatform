package com.gestor.chef.gf.application.service;

import com.gestor.chef.gf.application.service.support.EntityFinder;
import com.gestor.chef.gf.domain.model.Product;
import com.gestor.chef.gf.domain.model.value.DomainValues;
import com.gestor.chef.gf.domain.port.in.ProductUseCase;
import com.gestor.chef.gf.domain.port.out.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements ProductUseCase {

    private final ProductRepositoryPort productRepository;

    @Override
    @Caching(evict = {
        @CacheEvict(value = "products", allEntries = true),
        @CacheEvict(value = "products_low_stock", allEntries = true),
        @CacheEvict(value = "products_by_category", allEntries = true),
        @CacheEvict(value = "products_by_supplier", allEntries = true)
    })
    public Product createProduct(Product product) {
        Instant now = Instant.now();
        product.setStatus(DomainValues.Status.ACTIVE);
        product.setCreatedAt(now);
        product.setUpdatedAt(now);
        return productRepository.save(product);
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "products", allEntries = true),
        @CacheEvict(value = "products_low_stock", allEntries = true),
        @CacheEvict(value = "products_by_category", allEntries = true),
        @CacheEvict(value = "products_by_supplier", allEntries = true)
    })
    public Product updateProduct(String id, Product product) {
        Product existing = EntityFinder.required(productRepository.findById(id), "Producto", id);
        mergeProduct(existing, product);
        existing.setUpdatedAt(Instant.now());
        return productRepository.save(existing);
    }

    @Override
    @Cacheable(value = "products", key = "#id")
    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    @Override
    @Cacheable(value = "products", unless = "#result.isEmpty()")
    public List<Product> getAllProducts() {
        return productRepository.findByStatus(DomainValues.Status.ACTIVE);
    }

    @Override
    public Page<Product> getAllProductsPaged(Pageable pageable) {
        return productRepository.findAllPaged(pageable);
    }

    @Override
    @Cacheable(value = "products_by_category", key = "#category", unless = "#result.isEmpty()")
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryAndStatus(category, DomainValues.Status.ACTIVE);
    }

    @Override
    @Cacheable(value = "products_by_supplier", key = "#supplierId", unless = "#result.isEmpty()")
    public List<Product> getProductsBySupplier(String supplierId) {
        return productRepository.findBySupplierIdAndStatus(supplierId, DomainValues.Status.ACTIVE);
    }

    @Override
    @Cacheable(value = "products_low_stock", unless = "#result.isEmpty()")
    public List<Product> getLowStockProducts() {
        return productRepository.findLowStockProducts();
    }

    @Override
    public List<Product> getExpiringProducts(LocalDate before) {
        return productRepository.findByExpirationDateBefore(before);
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "products", allEntries = true),
        @CacheEvict(value = "products_low_stock", allEntries = true),
        @CacheEvict(value = "products_by_category", allEntries = true),
        @CacheEvict(value = "products_by_supplier", allEntries = true)
    })
    public void deleteProduct(String id) {
        Product product = EntityFinder.required(productRepository.findById(id), "Producto", id);
        product.setStatus(DomainValues.Status.INACTIVE);
        product.setUpdatedAt(Instant.now());
        productRepository.save(product);
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "products", allEntries = true),
        @CacheEvict(value = "products_low_stock", allEntries = true),
        @CacheEvict(value = "products_by_category", allEntries = true),
        @CacheEvict(value = "products_by_supplier", allEntries = true)
    })
    public Product updateStock(String id, double newQuantity) {
        if (newQuantity < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }
        Product product = EntityFinder.required(productRepository.findById(id), "Producto", id);
        product.setQuantity(newQuantity);
        product.setUpdatedAt(Instant.now());
        return productRepository.save(product);
    }

    private void mergeProduct(Product existing, Product update) {
        if (update.getName() != null) existing.setName(update.getName());
        if (update.getCategory() != null) existing.setCategory(update.getCategory());
        if (update.getSupplierId() != null) existing.setSupplierId(update.getSupplierId());
        if (update.getSupplierName() != null) existing.setSupplierName(update.getSupplierName());
        if (update.getPrice() != null) existing.setPrice(update.getPrice());
        if (update.getUnit() != null) existing.setUnit(update.getUnit());
        if (update.getDescription() != null) existing.setDescription(update.getDescription());
        if (update.getExpirationDate() != null) existing.setExpirationDate(update.getExpirationDate());
        if (update.getMinimumQuantity() > 0) existing.setMinimumQuantity(update.getMinimumQuantity());
    }
}
