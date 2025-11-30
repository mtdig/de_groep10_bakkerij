package com.example.bakkerij.repository;

import com.example.bakkerij.model.Order;
import com.example.bakkerij.model.OrderItem;
import com.example.bakkerij.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class OrderRepositoryTest {

    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository = new OrderRepository();
    }

    @Test
    void shouldAddOrder() {
        String username = "john";
        Order order = createTestOrder(12345);

        orderRepository.addOrder(username, order);

        List<Order> orders = orderRepository.findByUsername(username);
        assertThat(orders).hasSize(1);
        assertThat(orders.get(0)).isEqualTo(order);
    }

    @Test
    void shouldFindOrdersByUsername() {
        String username = "john";
        Order order1 = createTestOrder(12345);
        Order order2 = createTestOrder(12346);

        orderRepository.addOrder(username, order1);
        orderRepository.addOrder(username, order2);

        List<Order> orders = orderRepository.findByUsername(username);
        assertThat(orders).hasSize(2);
        assertThat(orders).containsExactly(order2, order1); // Added at beginning, so reversed
    }

    @Test
    void shouldReturnEmptyListForUserWithNoOrders() {
        List<Order> orders = orderRepository.findByUsername("nonexistent");

        assertThat(orders).isEmpty();
    }

    @Test
    void shouldKeepOrdersSeparateByUser() {
        Order order1 = createTestOrder(12345);
        Order order2 = createTestOrder(12346);

        orderRepository.addOrder("john", order1);
        orderRepository.addOrder("jane", order2);

        assertThat(orderRepository.findByUsername("john")).containsExactly(order1);
        assertThat(orderRepository.findByUsername("jane")).containsExactly(order2);
    }

    @Test
    void shouldSetOrderHistory() {
        String username = "john";
        List<Order> orders = List.of(
                createTestOrder(12345),
                createTestOrder(12346)
        );

        orderRepository.setOrderHistory(username, orders);

        List<Order> result = orderRepository.findByUsername(username);
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(orders);
    }

    @Test
    void shouldOverwritePreviousHistoryWhenSettingNewHistory() {
        String username = "john";
        orderRepository.addOrder(username, createTestOrder(11111));

        List<Order> newHistory = List.of(createTestOrder(22222));
        orderRepository.setOrderHistory(username, newHistory);

        List<Order> result = orderRepository.findByUsername(username);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOrderNumber()).isEqualTo(22222);
    }

    @Test
    void shouldCheckIfUserHasOrderHistory() {
        String username = "john";
        assertThat(orderRepository.hasOrderHistory(username)).isFalse();

        orderRepository.addOrder(username, createTestOrder(12345));

        assertThat(orderRepository.hasOrderHistory(username)).isTrue();
    }

    private Order createTestOrder(int orderNumber) {
        Product product = new Product(1, "Brood", "Brood", "Brood", "Brood", "Brood", "Brood",
                "Desc", "Desc", "Desc", "Desc", "Desc", "Desc",
                2.50, "image.jpg", "brood");
        List<OrderItem> items = List.of(new OrderItem(product, 2));
        return new Order(orderNumber, "30/11/2025", items, 5.00);
    }
}
