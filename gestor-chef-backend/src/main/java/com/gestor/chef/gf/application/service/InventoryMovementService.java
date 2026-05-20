package com.gestor.chef.gf.application.service;

import com.gestor.chef.gf.domain.model.Inventory;
import com.gestor.chef.gf.domain.model.InventoryMovement;
import com.gestor.chef.gf.domain.model.Product;
import com.gestor.chef.gf.domain.model.value.DomainValues;
import com.gestor.chef.gf.domain.port.in.InventoryMovementUseCase;
import com.gestor.chef.gf.domain.port.in.ProductUseCase;
import com.gestor.chef.gf.domain.port.out.InventoryMovementRepositoryPort;
import com.gestor.chef.gf.domain.port.out.InventoryRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.gestor.chef.gf.application.service.support.DomainValidator.requireAllowed;
import static com.gestor.chef.gf.application.service.support.DomainValidator.requirePositive;

@Service
@RequiredArgsConstructor
public class InventoryMovementService implements InventoryMovementUseCase {

    private final InventoryMovementRepositoryPort movementRepository;
    private final InventoryRepositoryPort inventoryRepository;
    private final ProductUseCase productUseCase;

    @Override
    public InventoryMovement registerMovement(InventoryMovement movement) {
        movement.setMovementType(requireAllowed(movement.getMovementType(), DomainValues.MovementType.ALL, "movementType"));
        requirePositive(movement.getQuantityChanged(), "quantityChanged");
        movement.setTimestamp(Instant.now());
        productUseCase.getProductById(movement.getProductId()).ifPresent(product -> applyStockMovement(product, movement));
        return movementRepository.save(movement);
    }

    @Override
    public Optional<InventoryMovement> getMovementById(String id) {
        return movementRepository.findById(id);
    }

    @Override
    public List<InventoryMovement> getAllMovements() {
        return movementRepository.findAll();
    }

    @Override
    public Page<InventoryMovement> getAllMovementsPaged(Pageable pageable) {
        return movementRepository.findAllPaged(pageable);
    }

    @Override
    public List<InventoryMovement> getMovementsByProduct(String productId) {
        return movementRepository.findByProductId(productId);
    }

    @Override
    public List<InventoryMovement> getMovementsByType(String movementType) {
        return movementRepository.findByMovementType(requireAllowed(movementType, DomainValues.MovementType.ALL, "movementType"));
    }

    @Override
    public List<InventoryMovement> getMovementsByDateRange(Instant from, Instant to) {
        return movementRepository.findByTimestampBetween(from, to);
    }

    @Override
    public List<InventoryMovement> getMovementsByUser(String userId) {
        return movementRepository.findByUserId(userId);
    }

    @Override
    public InventoryMovement registerScaleWeight(String productId, double weight, String userId) {
        InventoryMovement movement = InventoryMovement.builder()
                .productId(productId)
                .quantityChanged(weight)
                .movementType(DomainValues.MovementType.IN)
                .reason(DomainValues.MovementReason.SCALE_MEASUREMENT)
                .userId(userId)
                .weightFromScale(weight)
                .notes("Peso registrado desde balanza de precisión: " + weight + " kg")
                .build();
        return registerMovement(movement);
    }

    private void applyStockMovement(Product product, InventoryMovement movement) {
        double updatedQuantity = calculateUpdatedQuantity(product.getQuantity(), movement);
        productUseCase.updateStock(product.getId(), updatedQuantity);
        inventoryRepository.findByProductId(product.getId()).ifPresent(inventory -> updateInventorySnapshot(inventory, updatedQuantity));
        movement.setProductName(product.getName());
    }

    private double calculateUpdatedQuantity(double currentQuantity, InventoryMovement movement) {
        return switch (movement.getMovementType()) {
            case DomainValues.MovementType.IN -> currentQuantity + movement.getQuantityChanged();
            case DomainValues.MovementType.OUT, DomainValues.MovementType.WASTE -> subtractStock(currentQuantity, movement.getQuantityChanged());
            case DomainValues.MovementType.ADJUSTMENT -> movement.getQuantityChanged();
            default -> currentQuantity;
        };
    }

    private double subtractStock(double currentQuantity, double requestedQuantity) {
        if (requestedQuantity > currentQuantity) {
            throw new IllegalStateException("Stock insuficiente para registrar la salida de inventario");
        }
        return currentQuantity - requestedQuantity;
    }

    private void updateInventorySnapshot(Inventory inventory, double updatedQuantity) {
        inventory.setCurrentQuantity(updatedQuantity);
        inventory.setLowStockAlert(updatedQuantity < inventory.getMinimumQuantity());
        inventory.setLastUpdated(Instant.now());
        inventoryRepository.save(inventory);
    }
}
