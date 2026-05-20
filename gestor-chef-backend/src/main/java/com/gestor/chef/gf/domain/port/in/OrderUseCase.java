package com.gestor.chef.gf.domain.port.in;

import com.gestor.chef.gf.domain.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface OrderUseCase {
    Order createOrder(Order order);
    Order updateOrder(String id, Order order);
    Optional<Order> getOrderById(String id);
    List<Order> getAllOrders();
    Page<Order> getAllOrdersPaged(Pageable pageable);
    List<Order> getOrdersByStatus(String status);
    List<Order> getOrdersByDateRange(Instant from, Instant to);
    List<Order> getOrdersByTable(String tableNumber);
    Order updateOrderStatus(String id, String status, String userId);
    void cancelOrder(String id, String userId);
    Order processWhatsAppOrder(String rawMessage, String senderPhone);
}
