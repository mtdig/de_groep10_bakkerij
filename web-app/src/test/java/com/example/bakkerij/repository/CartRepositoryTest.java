package com.example.bakkerij.repository;

import com.example.bakkerij.model.Cart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CartRepositoryTest {

    private CartRepository cartRepository;

    @BeforeEach
    void setUp() {
        cartRepository = new CartRepository();
    }

    @Test
    void shouldCreateNewCartForNewSession() {
        String sessionId = "session123";

        Cart cart = cartRepository.getCart(sessionId);

        assertThat(cart).isNotNull();
        assertThat(cart.getItems()).isEmpty();
    }

    @Test
    void shouldReturnSameCartForSameSession() {
        String sessionId = "session123";

        Cart cart1 = cartRepository.getCart(sessionId);
        cart1.addItem(1, 2);

        Cart cart2 = cartRepository.getCart(sessionId);

        assertThat(cart2).isSameAs(cart1);
        assertThat(cart2.getQuantity(1)).isEqualTo(2);
    }

    @Test
    void shouldReturnDifferentCartsForDifferentSessions() {
        String session1 = "session123";
        String session2 = "session456";

        Cart cart1 = cartRepository.getCart(session1);
        cart1.addItem(1, 2);

        Cart cart2 = cartRepository.getCart(session2);

        assertThat(cart2).isNotSameAs(cart1);
        assertThat(cart2.getQuantity(1)).isZero();
    }

    @Test
    void shouldClearCart() {
        String sessionId = "session123";
        Cart cart = cartRepository.getCart(sessionId);
        cart.addItem(1, 2);
        cart.addItem(2, 3);

        cartRepository.clearCart(sessionId);

        Cart clearedCart = cartRepository.getCart(sessionId);
        assertThat(clearedCart.getItems()).isEmpty();
    }

    @Test
    void shouldHandleClearingNonExistentCart() {
        // Should not throw exception
        assertThatCode(() -> cartRepository.clearCart("nonexistent"))
                .doesNotThrowAnyException();
    }
}
