package org.example.models;

import org.example.enums.OrderStatus;
import org.example.enums.UserRole;
import org.example.observer.NotificationObserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Member extends Account implements NotificationObserver {
    private ShoppingCart cart;
    private List<Order> orders;

    public Member(String userName, String password, String name, Address address) {
        super(userName, password, name, address, UserRole.MEMBER);
        this.cart = new ShoppingCart();
        this.orders = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public void update(String message) {
        System.out.println("Notification for " + getName() + ": " + message);
    }

    public OrderStatus placeOrder(Order order) {
        orders.add(order);
        order.setStatus(OrderStatus.PENDING);
        System.out.println("Order " + order.getOrderNumber() + " placed by " + getName());
        return OrderStatus.PENDING;
    }

    public boolean cancelOrder(Order order) {
        if (order.getStatus() == OrderStatus.UNSHIPPED || order.getStatus() == OrderStatus.PENDING) {
            order.setStatus(OrderStatus.CANCELED);
            System.out.println("Order " + order.getOrderNumber() + " canceled by " + getName());
            return true;
        }
        System.out.println("Cannot cancel order " + order.getOrderNumber() + " - status: " + order.getStatus());
        return false;
    }

    public ShoppingCart getCart() { return cart; }
    public void setCart(ShoppingCart cart) { this.cart = cart; }
    public List<Order> getOrders() { return orders; }
}
