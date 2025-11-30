package com.example.bakkerij.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class CartTest {

    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = new Cart();
    }

    @Test
    void shouldStartEmpty() {
        assertThat(cart.isEmpty()).isTrue();
        assertThat(cart.getTotalItems()).isZero();
    }

    @Test
    void shouldAddItem() {
        cart.addItem(1, 2);

        assertThat(cart.getQuantity(1)).isEqualTo(2);
        assertThat(cart.getTotalItems()).isEqualTo(2);
        assertThat(cart.isEmpty()).isFalse();
    }

    @Test
    void shouldAddToExistingItem() {
        cart.addItem(1, 2);
        cart.addItem(1, 3);

        assertThat(cart.getQuantity(1)).isEqualTo(5);
        assertThat(cart.getTotalItems()).isEqualTo(5);
    }

    @Test
    void shouldUpdateItemQuantity() {
        cart.addItem(1, 5);
        cart.updateItem(1, 3);

        assertThat(cart.getQuantity(1)).isEqualTo(3);
        assertThat(cart.getTotalItems()).isEqualTo(3);
    }

    @Test
    void shouldRemoveItemWhenUpdatingToZero() {
        cart.addItem(1, 5);
        cart.updateItem(1, 0);

        assertThat(cart.getQuantity(1)).isZero();
        assertThat(cart.isEmpty()).isTrue();
    }

    @Test
    void shouldRemoveItemWhenUpdatingToNegative() {
        cart.addItem(1, 5);
        cart.updateItem(1, -1);

        assertThat(cart.getQuantity(1)).isZero();
        assertThat(cart.isEmpty()).isTrue();
    }

    @Test
    void shouldRemoveItem() {
        cart.addItem(1, 5);
        cart.removeItem(1);

        assertThat(cart.getQuantity(1)).isZero();
        assertThat(cart.isEmpty()).isTrue();
    }

    @Test
    void shouldHandleMultipleItems() {
        cart.addItem(1, 2);
        cart.addItem(2, 3);
        cart.addItem(3, 1);

        assertThat(cart.getTotalItems()).isEqualTo(6);
        assertThat(cart.getQuantity(1)).isEqualTo(2);
        assertThat(cart.getQuantity(2)).isEqualTo(3);
        assertThat(cart.getQuantity(3)).isEqualTo(1);
    }

    @Test
    void shouldClearCart() {
        cart.addItem(1, 2);
        cart.addItem(2, 3);
        cart.clear();

        assertThat(cart.isEmpty()).isTrue();
        assertThat(cart.getTotalItems()).isZero();
    }

    @Test
    void shouldReturnCopyOfItems() {
        cart.addItem(1, 2);
        var items = cart.getItems();
        items.put(2, 5); // Modify the returned map

        assertThat(cart.getQuantity(2)).isZero(); // Original cart unchanged
    }
}
