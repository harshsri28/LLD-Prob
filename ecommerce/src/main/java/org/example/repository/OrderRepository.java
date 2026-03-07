package org.example.repository;

import org.example.enums.OrderStatus;
import org.example.models.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class OrderRepository {
    private Map<String, Order> orders = new HashMap<>();

    public void addOrder(Order order) {
        orders.put(order.getOrderNumber(), order);
    }

    public Optional<Order> getOrderById(String orderNumber) {
        return Optional.ofNullable(orders.get(orderNumber));
    }

    public void removeOrder(String orderNumber) {
        orders.remove(orderNumber);
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(orders.values());
    }

    public List<Order> findByStatus(OrderStatus status) {
        return orders.values().stream()
                .filter(o -> o.getStatus() == status)
                .collect(Collectors.toList());
    }
}
