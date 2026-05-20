package com.gestor.chef.gf.infrastructure.adapter.in.web;

import com.gestor.chef.gf.domain.model.Product;
import com.gestor.chef.gf.domain.port.in.ProductUseCase;
import com.gestor.chef.gf.infrastructure.adapter.in.web.dto.ApiResponse;
import com.gestor.chef.gf.infrastructure.adapter.in.web.dto.CreateProductRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Gestión del catálogo de productos del inventario")
public class ProductController {

    private final ProductUseCase productUseCase;

    @GetMapping
    @Operation(summary = "Listar todos los productos (paginado). Parámetros: page, size, sort")
    public ResponseEntity<ApiResponse<Page<Product>>> getAll(
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(productUseCase.getAllProductsPaged(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID")
    public ResponseEntity<ApiResponse<Product>> getById(@PathVariable String id) {
        return productUseCase.getProductById(id)
                .map(p -> ResponseEntity.ok(ApiResponse.ok(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear producto")
    public ResponseEntity<ApiResponse<Product>> create(@Valid @RequestBody CreateProductRequest req) {
        Product product = Product.builder()
                .name(req.getName()).category(req.getCategory())
                .supplierId(req.getSupplierId()).supplierName(req.getSupplierName())
                .quantity(req.getQuantity()).minimumQuantity(req.getMinimumQuantity())
                .unit(req.getUnit()).price(req.getPrice())
                .expirationDate(req.getExpirationDate()).description(req.getDescription())
                .build();
        return ResponseEntity.status(201).body(ApiResponse.ok("Producto creado", productUseCase.createProduct(product)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto")
    public ResponseEntity<ApiResponse<Product>> update(@PathVariable String id,
                                                        @Valid @RequestBody CreateProductRequest req) {
        Product product = Product.builder()
                .name(req.getName()).category(req.getCategory())
                .supplierId(req.getSupplierId()).supplierName(req.getSupplierName())
                .quantity(req.getQuantity()).minimumQuantity(req.getMinimumQuantity())
                .unit(req.getUnit()).price(req.getPrice())
                .expirationDate(req.getExpirationDate()).description(req.getDescription())
                .build();
        return ResponseEntity.ok(ApiResponse.ok(productUseCase.updateProduct(id, product)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        productUseCase.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.ok("Producto eliminado", null));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Filtrar por categoría")
    public ResponseEntity<ApiResponse<List<Product>>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(ApiResponse.ok(productUseCase.getProductsByCategory(category)));
    }

    @GetMapping("/supplier/{supplierId}")
    @Operation(summary = "Productos por proveedor")
    public ResponseEntity<ApiResponse<List<Product>>> getBySupplier(@PathVariable String supplierId) {
        return ResponseEntity.ok(ApiResponse.ok(productUseCase.getProductsBySupplier(supplierId)));
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Productos con stock bajo (quantity < minimumQuantity)")
    public ResponseEntity<ApiResponse<List<Product>>> getLowStock() {
        return ResponseEntity.ok(ApiResponse.ok(productUseCase.getLowStockProducts()));
    }

    @GetMapping("/expiring")
    @Operation(summary = "Productos próximos a vencer")
    public ResponseEntity<ApiResponse<List<Product>>> getExpiring(
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(ApiResponse.ok(productUseCase.getExpiringProducts(LocalDate.now().plusDays(days))));
    }

    @PatchMapping("/{id}/stock")
    @Operation(summary = "Actualizar stock directamente")
    public ResponseEntity<ApiResponse<Product>> updateStock(@PathVariable String id,
                                                             @RequestBody Map<String, Double> body) {
        Double qty = body.get("quantity");
        if (qty == null) return ResponseEntity.badRequest().body(ApiResponse.error("Falta el campo 'quantity'"));
        return ResponseEntity.ok(ApiResponse.ok(productUseCase.updateStock(id, qty)));
    }
}
