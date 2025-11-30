package com.example.bakkerij.service;

import com.example.bakkerij.model.Order;
import com.example.bakkerij.model.OrderItem;
import com.example.bakkerij.model.Product;
import com.example.bakkerij.repository.OrderRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final Random random = new Random();

    public OrderService(OrderRepository orderRepository, ProductService productService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
    }

    public Order createOrder(String username, List<OrderItem> items) {
        double total = items.stream()
            .mapToDouble(OrderItem::getSubtotal)
            .sum();
        
        int orderNumber = random.nextInt(90000) + 10000; // 5-digit order number
        String orderDate = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        
        Order order = new Order(orderNumber, orderDate, items, total);
        orderRepository.addOrder(username, order);
        
        return order;
    }

    public List<Order> getOrderHistory(String username) {
        return orderRepository.findByUsername(username);
    }

    public Optional<Order> findOrderByNumber(String username, int orderNumber) {
        return orderRepository.findByUsername(username).stream()
            .filter(o -> o.getOrderNumber() == orderNumber)
            .findFirst();
    }

    public List<Order> generateOrderHistory(String username) {
        if (orderRepository.hasOrderHistory(username)) {
            return orderRepository.findByUsername(username);
        }
        
        List<Order> orders = new ArrayList<>();
        int orderCount = random.nextInt(7); // 0-6 orders
        
        List<Product> broodProducts = productService.getAllProducts().stream()
            .filter(p -> "brood".equals(p.getCategory()))
            .toList();
        
        if (broodProducts.isEmpty()) {
            return orders;
        }
        
        for (int i = 0; i < orderCount; i++) {
            List<OrderItem> items = new ArrayList<>();
            int itemCount = random.nextInt(200) + 1; // 1-200 products
            
            int itemsRemaining = itemCount;
            while (itemsRemaining > 0) {
                Product product = broodProducts.get(random.nextInt(broodProducts.size()));
                int quantity = Math.min(random.nextInt(20) + 1, itemsRemaining);
                items.add(new OrderItem(product, quantity));
                itemsRemaining -= quantity;
            }
            
            double total = items.stream()
                .mapToDouble(OrderItem::getSubtotal)
                .sum();
            
            long daysAgo = random.nextInt(365);
            String date = java.time.LocalDate.now().minusDays(daysAgo).toString();
            
            orders.add(new Order(orderCount - i, date, items, total));
        }
        
        orderRepository.setOrderHistory(username, orders);
        return orders;
    }
}
