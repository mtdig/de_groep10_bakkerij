package com.example.bakkerij.model;

import java.util.HashMap;
import java.util.Map;

public class Cart {
    private final Map<Integer, Integer> items; // productId -> quantity

    public Cart() {
        this.items = new HashMap<>();
    }

    public void addItem(int productId, int quantity) {
        items.put(productId, items.getOrDefault(productId, 0) + quantity);
    }

    public void updateItem(int productId, int quantity) {
        if (quantity <= 0) {
            items.remove(productId);
        } else {
            items.put(productId, quantity);
        }
    }

    public void removeItem(int productId) {
        items.remove(productId);
    }

    public int getQuantity(int productId) {
        return items.getOrDefault(productId, 0);
    }

    public Map<Integer, Integer> getItems() {
        return new HashMap<>(items);
    }

    public int getTotalItems() {
        return items.values().stream().mapToInt(Integer::intValue).sum();
    }

    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
