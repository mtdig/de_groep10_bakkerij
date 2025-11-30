package com.example.bakkerij.service;

import com.example.bakkerij.model.Order;
import com.example.bakkerij.model.OrderItem;
import com.example.bakkerij.model.Product;
import com.example.bakkerij.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductService productService;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, productService);
    }

    @Test
    void shouldCreateOrder() {
        String username = "john";
        Product product = createTestProduct(1, "Brood", 2.50);
        List<OrderItem> items = List.of(new OrderItem(product, 3));

        Order order = orderService.createOrder(username, items);

        assertThat(order).isNotNull();
        assertThat(order.getOrderNumber()).isBetween(10000, 99999);
        assertThat(order.getTotal()).isEqualTo(7.50);
        assertThat(order.getItems()).hasSize(1);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).addOrder(eq(username), orderCaptor.capture());
        assertThat(orderCaptor.getValue()).isEqualTo(order);
    }

    @Test
    void shouldGetOrderHistory() {
        String username = "john";
        List<Order> orders = List.of(
                createTestOrder(12345),
                createTestOrder(12346)
        );
        when(orderRepository.findByUsername(username)).thenReturn(orders);

        List<Order> result = orderService.getOrderHistory(username);

        assertThat(result).hasSize(2);
        verify(orderRepository).findByUsername(username);
    }

    @Test
    void shouldFindOrderByNumber() {
        String username = "john";
        Order order = createTestOrder(12345);
        when(orderRepository.findByUsername(username)).thenReturn(List.of(order));

        Optional<Order> result = orderService.findOrderByNumber(username, 12345);

        assertThat(result).isPresent();
        assertThat(result.get().getOrderNumber()).isEqualTo(12345);
    }

    @Test
    void shouldReturnEmptyWhenOrderNotFound() {
        String username = "john";
        when(orderRepository.findByUsername(username)).thenReturn(List.of());

        Optional<Order> result = orderService.findOrderByNumber(username, 12345);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldGenerateOrderHistory() {
        String username = "john";
        Product product = createTestProduct(1, "Brood", 2.50);
        when(orderRepository.hasOrderHistory(username)).thenReturn(false);
        when(productService.getAllProducts()).thenReturn(List.of(product));

        List<Order> orders = orderService.generateOrderHistory(username);

        // Random generation can produce 0-6 orders
        assertThat(orders.size()).isBetween(0, 6);
        verify(orderRepository).setOrderHistory(eq(username), anyList());
    }

    @Test
    void shouldNotRegenerateExistingOrderHistory() {
        String username = "john";
        List<Order> existingOrders = List.of(createTestOrder(12345));
        when(orderRepository.hasOrderHistory(username)).thenReturn(true);
        when(orderRepository.findByUsername(username)).thenReturn(existingOrders);

        List<Order> orders = orderService.generateOrderHistory(username);

        assertThat(orders).isEqualTo(existingOrders);
        verify(orderRepository, never()).setOrderHistory(anyString(), anyList());
    }

    @Test
    void shouldHandleEmptyProductListWhenGeneratingHistory() {
        String username = "john";
        when(orderRepository.hasOrderHistory(username)).thenReturn(false);
        when(productService.getAllProducts()).thenReturn(List.of());

        List<Order> orders = orderService.generateOrderHistory(username);

        assertThat(orders).isEmpty();
    }

    private Product createTestProduct(int id, String name, double price) {
        return new Product(id, name, name, name, name, name, name,
                "Desc", "Desc", "Desc", "Desc", "Desc", "Desc",
                price, "image.jpg", "brood");
    }

    private Order createTestOrder(int orderNumber) {
        Product product = createTestProduct(1, "Brood", 2.50);
        List<OrderItem> items = List.of(new OrderItem(product, 2));
        return new Order(orderNumber, "30/11/2025", items, 5.00);
    }
}
