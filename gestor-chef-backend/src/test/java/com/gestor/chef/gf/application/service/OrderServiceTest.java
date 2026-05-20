package com.gestor.chef.gf.application.service;

import com.gestor.chef.gf.domain.model.Order;
import com.gestor.chef.gf.domain.model.OrderItem;
import com.gestor.chef.gf.domain.port.in.RecipeUseCase;
import com.gestor.chef.gf.domain.port.out.CustomerNotificationPort;
import com.gestor.chef.gf.domain.port.out.OrderRepositoryPort;
import com.gestor.chef.gf.domain.port.out.RealtimeNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService")
class OrderServiceTest {

    @Mock
    private OrderRepositoryPort orderRepository;

    @Mock
    private RecipeUseCase recipeUseCase;

    @Mock
    private CustomerNotificationPort customerNotificationPort;

    @Mock
    private RealtimeNotificationPort realtimeNotificationPort;

    @InjectMocks
    private OrderService orderService;

    private Order sampleOrder;
    private OrderItem sampleItem;

    @BeforeEach
    void setUp() {
        sampleItem = OrderItem.builder()
                .recipeId("rec-1")
                .dishName("Medallón de res")
                .quantity(2)
                .unitPrice(new BigDecimal("45000"))
                .build();
        sampleOrder = Order.builder()
                .id("order-1")
                .tableNumber("5")
                .status("PENDING")
                .channel("DIRECT")
                .items(List.of(sampleItem))
                .totalAmount(new BigDecimal("90000"))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("crea orden, calcula total y notifica")
    void createOrderSuccess() {
        when(recipeUseCase.checkIngredientsAvailability("rec-1", 2)).thenReturn(true);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId("order-gen-1");
            return order;
        });
        Order created = orderService.createOrder(Order.builder().tableNumber("3").items(List.of(sampleItem)).build());
        assertThat(created.getStatus()).isEqualTo("PENDING");
        assertThat(created.getChannel()).isEqualTo("DIRECT");
        assertThat(created.getTotalAmount()).isEqualByComparingTo("90000");
        assertThat(created.getCreatedAt()).isNotNull();
        verify(orderRepository).save(any(Order.class));
        verify(realtimeNotificationPort).sendOrderUpdate(any(Order.class));
    }

    @Test
    @DisplayName("rechaza orden cuando no hay stock")
    void createOrderInsufficientStockThrows() {
        when(recipeUseCase.checkIngredientsAvailability("rec-1", 2)).thenReturn(false);
        Order input = Order.builder().items(List.of(sampleItem)).build();
        assertThatThrownBy(() -> orderService.createOrder(input))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Stock insuficiente");
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("actualiza orden y notifica")
    void updateOrderSuccess() {
        when(orderRepository.findById("order-1")).thenReturn(Optional.of(sampleOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Order updated = orderService.updateOrder("order-1", Order.builder().notes("Sin sal").build());
        assertThat(updated.getNotes()).isEqualTo("Sin sal");
        verify(realtimeNotificationPort).sendOrderUpdate(any(Order.class));
    }

    @Test
    @DisplayName("cambia estado y notifica")
    void updateOrderStatusSuccess() {
        when(orderRepository.findById("order-1")).thenReturn(Optional.of(sampleOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Order result = orderService.updateOrderStatus("order-1", "IN_PROGRESS", "user-1");
        assertThat(result.getStatus()).isEqualTo("IN_PROGRESS");
        verify(realtimeNotificationPort).sendOrderUpdate(any(Order.class));
    }

    @Test
    @DisplayName("al completar descuenta ingredientes antes de guardar")
    void updateOrderStatusCompletedDiscountsIngredients() {
        when(orderRepository.findById("order-1")).thenReturn(Optional.of(sampleOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Order result = orderService.updateOrderStatus("order-1", "COMPLETED", "user-1");
        assertThat(result.getStatus()).isEqualTo("COMPLETED");
        verify(recipeUseCase).discountIngredientsFromInventory("rec-1", 2, "user-1");
    }

    @Test
    @DisplayName("cancela orden de forma lógica")
    void cancelOrderSuccess() {
        when(orderRepository.findById("order-1")).thenReturn(Optional.of(sampleOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        orderService.cancelOrder("order-1", "user-1");
        verify(orderRepository).save(any(Order.class));
        verify(realtimeNotificationPort).sendOrderUpdate(any(Order.class));
    }

    @Test
    @DisplayName("procesa orden de WhatsApp")
    void processWhatsAppOrderSuccess() {
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId("order-wa-1");
            return order;
        });
        Order result = orderService.processWhatsAppOrder("2 Medallones", "+57 310 000 9999");
        assertThat(result.getChannel()).isEqualTo("WHATSAPP");
        assertThat(result.getStatus()).isEqualTo("PENDING");
        verify(customerNotificationPort).sendOrderConfirmation(any(Order.class), any(String.class));
    }
}
