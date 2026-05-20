package com.gestor.chef.gf.application.service;

import com.gestor.chef.gf.domain.model.Product;
import com.gestor.chef.gf.domain.port.out.ProductRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService")
class ProductServiceTest {

    @Mock
    private ProductRepositoryPort productRepository;

    @InjectMocks
    private ProductService productService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = Product.builder()
                .id("prod-1")
                .name("Lomo de res")
                .category("CARNES")
                .quantity(15.0)
                .minimumQuantity(5.0)
                .unit("kg")
                .price(new BigDecimal("32000"))
                .expirationDate(LocalDate.now().plusDays(5))
                .status("ACTIVE")
                .build();
    }

    @Test
    @DisplayName("crea producto activo")
    void createProductSuccess() {
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Product result = productService.createProduct(sampleProduct);
        assertThat(result.getStatus()).isEqualTo("ACTIVE");
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("actualiza campos no nulos")
    void updateProductSuccess() {
        when(productRepository.findById("prod-1")).thenReturn(Optional.of(sampleProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Product update = Product.builder().name("Lomo premium").description("Corte especial").build();
        Product result = productService.updateProduct("prod-1", update);
        assertThat(result.getName()).isEqualTo("Lomo premium");
        assertThat(result.getDescription()).isEqualTo("Corte especial");
        assertThat(result.getQuantity()).isEqualTo(15.0);
    }

    @Test
    @DisplayName("obtiene producto por id")
    void getProductByIdFound() {
        when(productRepository.findById("prod-1")).thenReturn(Optional.of(sampleProduct));
        assertThat(productService.getProductById("prod-1")).isPresent();
    }

    @Test
    @DisplayName("lista productos activos")
    void getAllProductsReturnsActiveList() {
        when(productRepository.findByStatus("ACTIVE")).thenReturn(List.of(sampleProduct));
        List<Product> result = productService.getAllProducts();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo("CARNES");
    }

    @Test
    @DisplayName("lista productos con stock bajo")
    void getLowStockProductsReturnsList() {
        Product lowStock = Product.builder().id("prod-2").name("Crema de leche").quantity(1.0).minimumQuantity(2.0).build();
        when(productRepository.findLowStockProducts()).thenReturn(List.of(lowStock));
        assertThat(productService.getLowStockProducts()).hasSize(1);
    }

    @Test
    @DisplayName("lista productos próximos a vencer")
    void getExpiringProductsReturnsList() {
        LocalDate cutoff = LocalDate.now().plusDays(7);
        when(productRepository.findByExpirationDateBefore(cutoff)).thenReturn(List.of(sampleProduct));
        List<Product> result = productService.getExpiringProducts(cutoff);
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("actualiza stock")
    void updateStockUpdatesQuantity() {
        when(productRepository.findById("prod-1")).thenReturn(Optional.of(sampleProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Product result = productService.updateStock("prod-1", 30.0);
        assertThat(result.getQuantity()).isEqualTo(30.0);
        assertThat(result.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("rechaza stock negativo")
    void updateStockRejectsNegativeQuantity() {
        assertThatThrownBy(() -> productService.updateStock("prod-1", -1.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cantidad");
    }

    @Test
    @DisplayName("inactiva producto")
    void deleteProductInactivates() {
        when(productRepository.findById("prod-1")).thenReturn(Optional.of(sampleProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        productService.deleteProduct("prod-1");
        verify(productRepository).save(argThat(product -> "INACTIVE".equals(product.getStatus())));
    }

    @Test
    @DisplayName("filtra por categoría activa")
    void getProductsByCategoryReturnsFiltered() {
        when(productRepository.findByCategoryAndStatus("CARNES", "ACTIVE")).thenReturn(List.of(sampleProduct));
        List<Product> result = productService.getProductsByCategory("CARNES");
        assertThat(result).hasSize(1);
    }
}
