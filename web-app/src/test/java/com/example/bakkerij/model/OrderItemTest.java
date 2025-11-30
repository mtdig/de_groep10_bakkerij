package com.example.bakkerij.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class OrderItemTest {

    @Test
    void shouldCreateOrderItem() {
        Product product = createTestProduct(1, "Brood", 2.50);
        OrderItem item = new OrderItem(product, 3);

        assertThat(item.getProduct()).isEqualTo(product);
        assertThat(item.getQuantity()).isEqualTo(3);
    }

    @Test
    void shouldCalculateSubtotal() {
        Product product = createTestProduct(1, "Brood", 2.50);
        OrderItem item = new OrderItem(product, 4);

        assertThat(item.getSubtotal()).isEqualTo(10.00);
    }

    @Test
    void shouldThrowExceptionWhenProductIsNull() {
        assertThatThrownBy(() -> new OrderItem(null, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product cannot be null");
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsZero() {
        Product product = createTestProduct(1, "Brood", 2.50);

        assertThatThrownBy(() -> new OrderItem(product, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantity must be positive");
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsNegative() {
        Product product = createTestProduct(1, "Brood", 2.50);

        assertThatThrownBy(() -> new OrderItem(product, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantity must be positive");
    }

    @Test
    void shouldBeEqualWhenSameProductAndQuantity() {
        Product product = createTestProduct(1, "Brood", 2.50);
        OrderItem item1 = new OrderItem(product, 3);
        OrderItem item2 = new OrderItem(product, 3);

        assertThat(item1).isEqualTo(item2);
        assertThat(item1.hashCode()).isEqualTo(item2.hashCode());
    }

    @Test
    void shouldHaveValidToString() {
        Product product = createTestProduct(1, "Brood", 2.50);
        OrderItem item = new OrderItem(product, 3);

        String toString = item.toString();
        assertThat(toString).contains("product=1");
        assertThat(toString).contains("quantity=3");
        assertThat(toString).contains("subtotal=7.5");
    }

    private Product createTestProduct(int id, String name, double price) {
        return new Product(id, name, name, name, name, name, name,
                "Desc", "Desc", "Desc", "Desc", "Desc", "Desc",
                price, "image.jpg", "category");
    }
}
