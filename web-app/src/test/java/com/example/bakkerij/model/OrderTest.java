package com.example.bakkerij.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

class OrderTest {

    @Test
    void shouldCreateOrder() {
        List<OrderItem> items = List.of(
                new OrderItem(createTestProduct(1, "Brood", 2.50), 2)
        );
        Order order = new Order(12345, "30/11/2025 14:30", items, 5.00);

        assertThat(order.getOrderNumber()).isEqualTo(12345);
        assertThat(order.getDate()).isEqualTo("30/11/2025 14:30");
        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getTotal()).isEqualTo(5.00);
    }

    @Test
    void shouldCalculateTotalQuantity() {
        List<OrderItem> items = List.of(
                new OrderItem(createTestProduct(1, "Brood", 2.50), 2),
                new OrderItem(createTestProduct(2, "Croissant", 1.50), 5),
                new OrderItem(createTestProduct(3, "Baguette", 1.00), 3)
        );
        Order order = new Order(12345, "30/11/2025", items, 15.50);

        assertThat(order.getTotalQuantity()).isEqualTo(10);
    }

    @Test
    void shouldReturnImmutableItemsList() {
        List<OrderItem> items = List.of(
                new OrderItem(createTestProduct(1, "Brood", 2.50), 2)
        );
        Order order = new Order(12345, "30/11/2025", items, 5.00);

        List<OrderItem> orderItems = order.getItems();
        assertThatThrownBy(() -> orderItems.add(new OrderItem(createTestProduct(2, "Test", 1.00), 1)))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldThrowExceptionWhenItemsAreNull() {
        assertThatThrownBy(() -> new Order(12345, "30/11/2025", null, 5.00))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Order must have at least one item");
    }

    @Test
    void shouldThrowExceptionWhenItemsAreEmpty() {
        assertThatThrownBy(() -> new Order(12345, "30/11/2025", List.of(), 5.00))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Order must have at least one item");
    }

    @Test
    void shouldBeEqualWhenSameOrderNumber() {
        List<OrderItem> items = List.of(new OrderItem(createTestProduct(1, "Brood", 2.50), 2));
        Order order1 = new Order(12345, "30/11/2025", items, 5.00);
        Order order2 = new Order(12345, "01/12/2025", items, 10.00);

        assertThat(order1).isEqualTo(order2);
        assertThat(order1.hashCode()).isEqualTo(order2.hashCode());
    }

    @Test
    void shouldHaveValidToString() {
        List<OrderItem> items = List.of(
                new OrderItem(createTestProduct(1, "Brood", 2.50), 2),
                new OrderItem(createTestProduct(2, "Croissant", 1.50), 3)
        );
        Order order = new Order(12345, "30/11/2025", items, 12.50);

        String toString = order.toString();
        assertThat(toString).contains("orderNumber=12345");
        assertThat(toString).contains("date='30/11/2025'");
        assertThat(toString).contains("items=2");
        assertThat(toString).contains("total=12.5");
    }

    private Product createTestProduct(int id, String name, double price) {
        return new Product(id, name, name, name, name, name, name,
                "Desc", "Desc", "Desc", "Desc", "Desc", "Desc",
                price, "image.jpg", "category");
    }
}
