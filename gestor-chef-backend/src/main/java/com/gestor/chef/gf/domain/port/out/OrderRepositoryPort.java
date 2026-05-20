package com.gestor.chef.gf.domain.port.out;

import com.gestor.chef.gf.domain.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface OrderRepositoryPort {
    Order save(Order order);
    Optional<Order> findById(String id);
    List<Order> findAll();
    Page<Order> findAllPaged(Pageable pageable);
    List<Order> findByStatus(String status);
    List<Order> findByCreatedAtBetween(Instant from, Instant to);
    List<Order> findByTableNumber(String tableNumber);
    void deleteById(String id);
}
