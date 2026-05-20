package com.gestor.chef.gf.infrastructure.adapter.in.web;

import com.gestor.chef.gf.domain.model.Supplier;
import com.gestor.chef.gf.domain.port.in.SupplierUseCase;
import com.gestor.chef.gf.infrastructure.adapter.in.web.dto.ApiResponse;
import com.gestor.chef.gf.infrastructure.adapter.in.web.dto.CreateSupplierRequest;
import com.gestor.chef.gf.infrastructure.adapter.in.web.dto.StatusUpdateRequest;
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
import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/suppliers")
@RequiredArgsConstructor
@Tag(name = "Proveedores", description = "Gestión de proveedores y órdenes de compra")
public class SupplierController {

    private final SupplierUseCase supplierUseCase;

    @GetMapping
    @Operation(summary = "Listar todos los proveedores (paginado). Parámetros: page, size, sort")
    public ResponseEntity<ApiResponse<Page<Supplier>>> getAll(
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(supplierUseCase.getAllSuppliersPaged(pageable)));
    }

    @GetMapping("/active")
    @Operation(summary = "Proveedores activos")
    public ResponseEntity<ApiResponse<List<Supplier>>> getActive() {
        return ResponseEntity.ok(ApiResponse.ok(supplierUseCase.getActiveSuppliers()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Supplier>> getById(@PathVariable String id) {
        return supplierUseCase.getSupplierById(id)
                .map(s -> ResponseEntity.ok(ApiResponse.ok(s)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear proveedor")
    public ResponseEntity<ApiResponse<Supplier>> create(@Valid @RequestBody CreateSupplierRequest req) {
        Supplier supplier = Supplier.builder()
                .name(req.getName()).contactName(req.getContactName())
                .email(req.getEmail()).phone(req.getPhone()).address(req.getAddress())
                .build();
        return ResponseEntity.status(201).body(ApiResponse.ok("Proveedor creado", supplierUseCase.createSupplier(supplier)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Supplier>> update(@PathVariable String id,
                                                         @Valid @RequestBody CreateSupplierRequest req) {
        Supplier supplier = Supplier.builder()
                .name(req.getName()).contactName(req.getContactName())
                .email(req.getEmail()).phone(req.getPhone()).address(req.getAddress())
                .build();
        return ResponseEntity.ok(ApiResponse.ok(supplierUseCase.updateSupplier(id, supplier)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        supplierUseCase.deleteSupplier(id);
        return ResponseEntity.ok(ApiResponse.ok("Proveedor eliminado", null));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Cambiar estado: ACTIVE | INACTIVE")
    public ResponseEntity<ApiResponse<Supplier>> changeStatus(@PathVariable String id,
                                                               @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(supplierUseCase.changeStatus(id, request.getStatus())));
    }
}
