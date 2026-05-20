package com.gestor.chef.gf.application.service;

import com.gestor.chef.gf.application.service.support.EntityFinder;
import com.gestor.chef.gf.domain.model.Order;
import com.gestor.chef.gf.domain.model.OrderItem;
import com.gestor.chef.gf.domain.model.value.DomainValues;
import com.gestor.chef.gf.domain.port.in.OrderUseCase;
import com.gestor.chef.gf.domain.port.in.RecipeUseCase;
import com.gestor.chef.gf.domain.port.out.CustomerNotificationPort;
import com.gestor.chef.gf.domain.port.out.OrderRepositoryPort;
import com.gestor.chef.gf.domain.port.out.RealtimeNotificationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.gestor.chef.gf.application.service.support.DomainValidator.requireAllowed;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService implements OrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final RecipeUseCase recipeUseCase;
    private final CustomerNotificationPort customerNotificationPort;
    private final RealtimeNotificationPort realtimeNotificationPort;

    @Override
    public Order createOrder(Order order) {
        validateIngredientsAvailability(order);
        order.setStatus(DomainValues.Status.PENDING);
        order.setChannel(order.getChannel() != null ? order.getChannel() : DomainValues.Channel.DIRECT);
        order.setCreatedAt(Instant.now());
        order.setUpdatedAt(Instant.now());
        order.setTotalAmount(calculateTotal(order));
        Order saved = orderRepository.save(order);
        realtimeNotificationPort.sendOrderUpdate(saved);
        return saved;
    }

    @Override
    public Order updateOrder(String id, Order order) {
        Order existing = EntityFinder.required(orderRepository.findById(id), "Orden", id);
        if (order.getTableNumber() != null) existing.setTableNumber(order.getTableNumber());
        if (order.getItems() != null) existing.setItems(order.getItems());
        if (order.getNotes() != null) existing.setNotes(order.getNotes());
        existing.setUpdatedAt(Instant.now());
        Order saved = orderRepository.save(existing);
        realtimeNotificationPort.sendOrderUpdate(saved);
        return saved;
    }

    @Override
    public Optional<Order> getOrderById(String id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Page<Order> getAllOrdersPaged(Pageable pageable) {
        return orderRepository.findAllPaged(pageable);
    }

    @Override
    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(requireAllowed(status, DomainValues.Status.ORDER, "status"));
    }

    @Override
    public List<Order> getOrdersByDateRange(Instant from, Instant to) {
        return orderRepository.findByCreatedAtBetween(from, to);
    }

    @Override
    public List<Order> getOrdersByTable(String tableNumber) {
        return orderRepository.findByTableNumber(tableNumber);
    }

    @Override
    public Order updateOrderStatus(String id, String status, String userId) {
        String normalizedStatus = requireAllowed(status, DomainValues.Status.ORDER, "status");
        Order order = EntityFinder.required(orderRepository.findById(id), "Orden", id);
        String previousStatus = order.getStatus();
        if (DomainValues.Status.COMPLETED.equals(normalizedStatus)) {
            discountOrderIngredients(order, userId);
        }
        order.setStatus(normalizedStatus);
        order.setUpdatedAt(Instant.now());
        Order saved = orderRepository.save(order);
        log.info("Orden {} cambió de {} a {}", id, previousStatus, normalizedStatus);
        realtimeNotificationPort.sendOrderUpdate(saved);
        notifyCustomerWhenApplicable(saved, normalizedStatus);
        return saved;
    }

    @Override
    public void cancelOrder(String id, String userId) {
        Order order = EntityFinder.required(orderRepository.findById(id), "Orden", id);
        order.setStatus(DomainValues.Status.CANCELLED);
        order.setUpdatedAt(Instant.now());
        Order saved = orderRepository.save(order);
        realtimeNotificationPort.sendOrderUpdate(saved);
    }

    @Override
    public Order processWhatsAppOrder(String rawMessage, String senderPhone) {
        Order order = Order.builder()
                .status(DomainValues.Status.PENDING)
                .channel(DomainValues.Channel.WHATSAPP)
                .notes("Pedido WhatsApp de " + senderPhone + ": " + rawMessage)
                .totalAmount(BigDecimal.ZERO)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        Order saved = orderRepository.save(order);
        realtimeNotificationPort.sendOrderUpdate(saved);
        customerNotificationPort.sendOrderConfirmation(saved, senderPhone);
        return saved;
    }

    private void validateIngredientsAvailability(Order order) {
        if (order.getItems() == null) {
            return;
        }
        order.getItems().forEach(item -> {
            if (item.getRecipeId() != null) {
                boolean available = recipeUseCase.checkIngredientsAvailability(item.getRecipeId(), item.getQuantity());
                if (!available) {
                    throw new IllegalStateException("Stock insuficiente para el plato: " + dishLabel(item));
                }
            }
        });
    }

    private BigDecimal calculateTotal(Order order) {
        if (order.getItems() == null || order.getItems().isEmpty()) {
            return order.getTotalAmount();
        }
        return order.getItems().stream()
                .filter(item -> item.getUnitPrice() != null)
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void discountOrderIngredients(Order order, String userId) {
        if (order.getItems() == null) {
            return;
        }
        for (OrderItem item : order.getItems()) {
            if (item.getRecipeId() != null) {
                recipeUseCase.discountIngredientsFromInventory(item.getRecipeId(), item.getQuantity(), userId);
            }
        }
    }

    private void notifyCustomerWhenApplicable(Order order, String status) {
        if (!DomainValues.Channel.WHATSAPP.equals(order.getChannel()) || order.getNotes() == null) {
            return;
        }
        String phone = extractPhoneFromNotes(order.getNotes());
        if (phone != null) {
            customerNotificationPort.sendOrderStatusUpdate(order, phone, status);
        }
    }

    private String dishLabel(OrderItem item) {
        return item.getDishName() != null ? item.getDishName() : item.getRecipeId();
    }

    private String extractPhoneFromNotes(String notes) {
        if (notes == null) {
            return null;
        }
        int deIndex = notes.indexOf("de ");
        int colonIndex = notes.indexOf(": ");
        if (deIndex >= 0 && colonIndex > deIndex) {
            return notes.substring(deIndex + 3, colonIndex).trim();
        }
        return null;
    }
}
