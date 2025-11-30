package com.example.bakkerij.service;

import com.example.bakkerij.model.Cart;
import com.example.bakkerij.model.OrderItem;
import com.example.bakkerij.repository.CartRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartService {
    private final CartRepository cartRepository;
    private final ProductService productService;

    public CartService(CartRepository cartRepository, ProductService productService) {
        this.cartRepository = cartRepository;
        this.productService = productService;
    }

    public void addToCart(String sessionId, int productId, int quantity) {
        Cart cart = cartRepository.getCart(sessionId);
        cart.addItem(productId, quantity);
    }

    public void updateCartItem(String sessionId, int productId, int quantity) {
        Cart cart = cartRepository.getCart(sessionId);
        cart.updateItem(productId, quantity);
    }

    public void removeFromCart(String sessionId, int productId) {
        Cart cart = cartRepository.getCart(sessionId);
        cart.removeItem(productId);
    }

    public int getCartCount(String sessionId) {
        Cart cart = cartRepository.getCart(sessionId);
        return cart.getTotalItems();
    }

    public List<Map<String, Object>> getCartItems(String sessionId) {
        Cart cart = cartRepository.getCart(sessionId);
        List<Map<String, Object>> cartItems = new ArrayList<>();
        
        for (Map.Entry<Integer, Integer> entry : cart.getItems().entrySet()) {
            int productId = entry.getKey();
            int quantity = entry.getValue();
            
            productService.getProductById(productId).ifPresent(product -> {
                Map<String, Object> item = new HashMap<>();
                item.put("product", product);
                item.put("quantity", quantity);
                item.put("subtotal", product.getPrice() * quantity);
                cartItems.add(item);
            });
        }
        
        return cartItems;
    }

    public double getCartTotal(String sessionId) {
        return getCartItems(sessionId).stream()
            .mapToDouble(item -> (Double) item.get("subtotal"))
            .sum();
    }

    public List<OrderItem> getOrderItems(String sessionId) {
        Cart cart = cartRepository.getCart(sessionId);
        List<OrderItem> items = new ArrayList<>();
        
        for (Map.Entry<Integer, Integer> entry : cart.getItems().entrySet()) {
            productService.getProductById(entry.getKey()).ifPresent(product -> {
                items.add(new OrderItem(product, entry.getValue()));
            });
        }
        
        return items;
    }

    public void clearCart(String sessionId) {
        cartRepository.clearCart(sessionId);
    }

    public boolean isCartEmpty(String sessionId) {
        Cart cart = cartRepository.getCart(sessionId);
        return cart.isEmpty();
    }
}
