package com.example.bakkerij.model;

import java.util.List;
import java.util.Objects;

public class Order {
    private final int orderNumber;
    private final String date;
    private final List<OrderItem> items;
    private final double total;

    public Order(int orderNumber, String date, List<OrderItem> items, double total) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
        this.orderNumber = orderNumber;
        this.date = date;
        this.items = List.copyOf(items); // Immutable copy
        this.total = total;
    }

    public int getOrderNumber() { return orderNumber; }
    public String getDate() { return date; }
    public List<OrderItem> getItems() { return items; }
    public double getTotal() { return total; }
    
    public int getTotalQuantity() {
        return items.stream().mapToInt(OrderItem::getQuantity).sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return orderNumber == order.orderNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderNumber);
    }

    @Override
    public String toString() {
        return "Order{orderNumber=" + orderNumber + ", date='" + date + "', items=" + items.size() + ", total=" + total + "}";
    }
}
