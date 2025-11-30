package com.example.bakkerij.repository;

import com.example.bakkerij.model.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderRepository {
    private final Map<String, List<Order>> orderHistory = new HashMap<>();

    public List<Order> findByUsername(String username) {
        return orderHistory.getOrDefault(username, new ArrayList<>());
    }

    public void addOrder(String username, Order order) {
        List<Order> orders = orderHistory.computeIfAbsent(username, k -> new ArrayList<>());
        orders.add(0, order); // Add to beginning
    }

    public void setOrderHistory(String username, List<Order> orders) {
        orderHistory.put(username, orders);
    }

    public boolean hasOrderHistory(String username) {
        return orderHistory.containsKey(username);
    }
}
