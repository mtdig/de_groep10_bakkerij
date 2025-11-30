package com.example;

import java.util.List;

public class Order {
    public int orderNumber;
    public String date;
    public List<OrderItem> items;
    public double total;

    public Order(int orderNumber, String date, List<OrderItem> items, double total) {
        this.orderNumber = orderNumber;
        this.date = date;
        this.items = items;
        this.total = total;
    }

    public int getOrderNumber() { return orderNumber; }
    public String getDate() { return date; }
    public List<OrderItem> getItems() { return items; }
    public double getTotal() { return total; }
}
