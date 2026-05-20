package com.gestor.chef.gf.application.service;

import com.gestor.chef.gf.domain.model.InventoryMovement;
import com.gestor.chef.gf.domain.model.Waste;
import com.gestor.chef.gf.domain.model.value.DomainValues;
import com.gestor.chef.gf.domain.port.in.InventoryMovementUseCase;
import com.gestor.chef.gf.domain.port.in.ProductUseCase;
import com.gestor.chef.gf.domain.port.in.WasteUseCase;
import com.gestor.chef.gf.domain.port.out.WasteRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WasteService implements WasteUseCase {

    private final WasteRepositoryPort wasteRepository;
    private final ProductUseCase productUseCase;
    private final InventoryMovementUseCase movementUseCase;

    @Override
    public Waste registerWaste(Waste waste) {
        waste.setRegisteredAt(Instant.now());
        if (waste.getOccurredAt() == null) {
            waste.setOccurredAt(Instant.now());
        }
        enrichWasteWithProductData(waste);
        InventoryMovement savedMovement = movementUseCase.registerMovement(buildWasteMovement(waste));
        waste.setInventoryMovementId(savedMovement.getId());
        Waste saved = wasteRepository.save(waste);
        log.info("Desperdicio registrado id={} producto={} cantidad={} causa={}",
                saved.getId(), saved.getProductName(), saved.getQuantityWasted(), saved.getCause());
        return saved;
    }

    @Override
    public Optional<Waste> getWasteById(String id) {
        return wasteRepository.findById(id);
    }

    @Override
    public List<Waste> getAllWastes() {
        return wasteRepository.findAll();
    }

    @Override
    public Page<Waste> getAllWastesPaged(Pageable pageable) {
        return wasteRepository.findAllPaged(pageable);
    }

    @Override
    public List<Waste> getWastesByProduct(String productId) {
        return wasteRepository.findByProductId(productId);
    }

    @Override
    public List<Waste> getWastesByCause(String cause) {
        return wasteRepository.findByCause(cause);
    }

    @Override
    public List<Waste> getWastesByUser(String userId) {
        return wasteRepository.findByReportedBy(userId);
    }

    @Override
    public List<Waste> getWastesByDateRange(Instant from, Instant to) {
        return wasteRepository.findByOccurredAtBetween(from, to);
    }

    private void enrichWasteWithProductData(Waste waste) {
        if (waste.getProductId() == null) {
            return;
        }
        productUseCase.getProductById(waste.getProductId()).ifPresent(product -> {
            if (waste.getEstimatedCost() == null) {
                BigDecimal unitPrice = product.getPrice() != null ? product.getPrice() : BigDecimal.ZERO;
                waste.setEstimatedCost(unitPrice.multiply(BigDecimal.valueOf(waste.getQuantityWasted())));
            }
            if (waste.getProductName() == null) {
                waste.setProductName(product.getName());
            }
        });
    }

    private InventoryMovement buildWasteMovement(Waste waste) {
        return InventoryMovement.builder()
                .productId(waste.getProductId())
                .productName(waste.getProductName())
                .quantityChanged(waste.getQuantityWasted())
                .movementType(DomainValues.MovementType.WASTE)
                .reason(causeToReason(waste.getCause()))
                .userId(waste.getReportedBy())
                .userName(waste.getReportedByName())
                .notes("Desperdicio: " + (waste.getDescription() != null ? waste.getDescription() : waste.getCause()))
                .build();
    }

    private String causeToReason(String cause) {
        if (cause == null) {
            return DomainValues.MovementReason.WASTE;
        }
        return switch (cause.toUpperCase()) {
            case DomainValues.WasteCause.EXPIRY -> DomainValues.MovementReason.WASTE_EXPIRY;
            case DomainValues.WasteCause.DETERIORATION -> DomainValues.MovementReason.WASTE_DETERIORATION;
            case DomainValues.WasteCause.KITCHEN_ACCIDENT -> DomainValues.MovementReason.WASTE_ACCIDENT;
            default -> DomainValues.MovementReason.WASTE;
        };
    }
}
