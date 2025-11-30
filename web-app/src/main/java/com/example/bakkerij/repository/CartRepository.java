package com.example.bakkerij.repository;

import com.example.bakkerij.model.Cart;

import java.util.HashMap;
import java.util.Map;

public class CartRepository {
    private final Map<String, Cart> carts = new HashMap<>();

    public Cart getCart(String sessionId) {
        return carts.computeIfAbsent(sessionId, k -> new Cart());
    }

    public void clearCart(String sessionId) {
        Cart cart = carts.get(sessionId);
        if (cart != null) {
            cart.clear();
        }
    }

    public void removeCart(String sessionId) {
        carts.remove(sessionId);
    }
}
