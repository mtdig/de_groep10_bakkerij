package com.example.bakkerij.service;

import com.example.bakkerij.model.Cart;
import com.example.bakkerij.model.OrderItem;
import com.example.bakkerij.model.Product;
import com.example.bakkerij.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductService productService;

    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartService = new CartService(cartRepository, productService);
    }

    @Test
    void shouldAddItemToCart() {
        String sessionId = "session123";
        Cart cart = new Cart();
        when(cartRepository.getCart(sessionId)).thenReturn(cart);

        cartService.addToCart(sessionId, 1, 2);

        assertThat(cart.getQuantity(1)).isEqualTo(2);
        verify(cartRepository).getCart(sessionId);
    }

    @Test
    void shouldUpdateCartItem() {
        String sessionId = "session123";
        Cart cart = new Cart();
        cart.addItem(1, 5);
        when(cartRepository.getCart(sessionId)).thenReturn(cart);

        cartService.updateCartItem(sessionId, 1, 3);

        assertThat(cart.getQuantity(1)).isEqualTo(3);
    }

    @Test
    void shouldRemoveItemFromCart() {
        String sessionId = "session123";
        Cart cart = new Cart();
        cart.addItem(1, 5);
        when(cartRepository.getCart(sessionId)).thenReturn(cart);

        cartService.removeFromCart(sessionId, 1);

        assertThat(cart.getQuantity(1)).isZero();
    }

    @Test
    void shouldGetCartCount() {
        String sessionId = "session123";
        Cart cart = new Cart();
        cart.addItem(1, 2);
        cart.addItem(2, 3);
        when(cartRepository.getCart(sessionId)).thenReturn(cart);

        int count = cartService.getCartCount(sessionId);

        assertThat(count).isEqualTo(5);
    }

    @Test
    void shouldGetCartItems() {
        String sessionId = "session123";
        Cart cart = new Cart();
        cart.addItem(1, 2);
        cart.addItem(2, 3);

        Product product1 = createTestProduct(1, "Brood", 2.50);
        Product product2 = createTestProduct(2, "Croissant", 1.50);

        when(cartRepository.getCart(sessionId)).thenReturn(cart);
        when(productService.getProductById(1)).thenReturn(Optional.of(product1));
        when(productService.getProductById(2)).thenReturn(Optional.of(product2));

        List<Map<String, Object>> items = cartService.getCartItems(sessionId);

        assertThat(items).hasSize(2);
        assertThat(items.get(0).get("quantity")).isEqualTo(2);
        assertThat(items.get(0).get("subtotal")).isEqualTo(5.00);
    }

    @Test
    void shouldCalculateCartTotal() {
        String sessionId = "session123";
        Cart cart = new Cart();
        cart.addItem(1, 2);
        cart.addItem(2, 3);

        Product product1 = createTestProduct(1, "Brood", 2.50);
        Product product2 = createTestProduct(2, "Croissant", 1.50);

        when(cartRepository.getCart(sessionId)).thenReturn(cart);
        when(productService.getProductById(1)).thenReturn(Optional.of(product1));
        when(productService.getProductById(2)).thenReturn(Optional.of(product2));

        double total = cartService.getCartTotal(sessionId);

        assertThat(total).isEqualTo(9.50); // (2 * 2.50) + (3 * 1.50)
    }

    @Test
    void shouldGetOrderItems() {
        String sessionId = "session123";
        Cart cart = new Cart();
        cart.addItem(1, 2);

        Product product = createTestProduct(1, "Brood", 2.50);
        when(cartRepository.getCart(sessionId)).thenReturn(cart);
        when(productService.getProductById(1)).thenReturn(Optional.of(product));

        List<OrderItem> items = cartService.getOrderItems(sessionId);

        assertThat(items).hasSize(1);
        assertThat(items.get(0).getProduct()).isEqualTo(product);
        assertThat(items.get(0).getQuantity()).isEqualTo(2);
    }

    @Test
    void shouldClearCart() {
        String sessionId = "session123";

        cartService.clearCart(sessionId);

        verify(cartRepository).clearCart(sessionId);
    }

    @Test
    void shouldCheckIfCartIsEmpty() {
        String sessionId = "session123";
        Cart emptyCart = new Cart();
        when(cartRepository.getCart(sessionId)).thenReturn(emptyCart);

        boolean isEmpty = cartService.isCartEmpty(sessionId);

        assertThat(isEmpty).isTrue();
    }

    @Test
    void shouldCheckIfCartIsNotEmpty() {
        String sessionId = "session123";
        Cart cart = new Cart();
        cart.addItem(1, 2);
        when(cartRepository.getCart(sessionId)).thenReturn(cart);

        boolean isEmpty = cartService.isCartEmpty(sessionId);

        assertThat(isEmpty).isFalse();
    }

    private Product createTestProduct(int id, String name, double price) {
        return new Product(id, name, name, name, name, name, name,
                "Desc", "Desc", "Desc", "Desc", "Desc", "Desc",
                price, "image.jpg", "category");
    }
}
