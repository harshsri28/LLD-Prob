package org.example.services;

import org.example.enums.NotificationType;
import org.example.enums.OrderStatus;
import org.example.enums.PaymentStatus;
import org.example.enums.ReturnStatus;
import org.example.enums.UserRole;
import org.example.models.Item;
import org.example.models.Member;
import org.example.models.Order;
import org.example.models.OrderLog;
import org.example.models.Product;
import org.example.models.Refund;
import org.example.models.ReturnRequest;
import org.example.models.ShoppingCart;
import org.example.observer.NotificationService;
import org.example.repository.OrderRepository;
import org.example.repository.ProductRepository;
import org.example.strategy.paymentStrategy.PaymentStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class OrderService {
    // Singleton with double-checked locking (same as BookingService in carRental)
    private static volatile OrderService instance;

    private OrderRepository orderRepo;
    private ProductRepository productRepo;
    private NotificationService notificationService;

    // Per-member locks to prevent double-ordering for the same member
    // while allowing different members to place orders concurrently
    private final Map<String, Object> memberLocks = new ConcurrentHashMap<>();

    private OrderService(OrderRepository orderRepo, ProductRepository productRepo, NotificationService notificationService) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
        this.notificationService = notificationService;
    }

    public static OrderService getInstance(OrderRepository orderRepo, ProductRepository productRepo, NotificationService notificationService) {
        if (instance == null) {
            synchronized (OrderService.class) {
                if (instance == null) {
                    instance = new OrderService(orderRepo, productRepo, notificationService);
                }
            }
        }
        return instance;
    }

    public Optional<Order> placeOrder(Member member, PaymentStrategy paymentStrategy) {
        // Only registered members can place orders
        if (member.getRole() == UserRole.GUEST) {
            System.out.println("Guests cannot place orders. Please register first.");
            return Optional.empty();
        }

        // BUG 3 FIX: Per-member lock prevents double-ordering the same cart
        Object lock = memberLocks.computeIfAbsent(member.getUserName(), k -> new Object());
        synchronized (lock) {
            ShoppingCart cart = member.getCart();
            List<Item> cartItems = cart.getItems();

            if (cartItems.isEmpty()) {
                System.out.println("Cart is empty. Cannot place order.");
                return Optional.empty();
            }

            // BUG 2 FIX: Track successfully decremented items for rollback
            List<Item> decrementedItems = new ArrayList<>();

            for (Item item : cartItems) {
                Optional<Product> product = productRepo.getProductById(item.getProductId());
                if (!product.isPresent()) {
                    System.out.println("Product not found: " + item.getProductId());
                    rollbackStock(decrementedItems);
                    return Optional.empty();
                }
                if (!product.get().decrementStock(item.getQuantity())) {
                    System.out.println("Insufficient stock for product: " + product.get().getName());
                    rollbackStock(decrementedItems);
                    return Optional.empty();
                }
                decrementedItems.add(item);
            }

            // Build order
            Order order = Order.builder()
                    .items(cartItems)
                    .totalAmount(cart.getTotalAmount())
                    .shippingAddress(member.getShippingAddress())
                    .build();

            // Process payment
            PaymentProcessor paymentProcessor = new PaymentProcessor(paymentStrategy);
            if (!paymentProcessor.pay(order)) {
                System.out.println("Payment failed for order: " + order.getOrderNumber());
                rollbackStock(decrementedItems);
                return Optional.empty();
            }

            // Place order
            member.placeOrder(order);
            order.addOrderLog(new OrderLog(order.getOrderNumber(), OrderStatus.PENDING, "Order placed successfully."));
            orderRepo.addOrder(order);

            // Clear cart after successful order
            cart.clear();

            // Notify
            notificationService.notifyObservers(NotificationType.ORDER_CONFIRMATION,
                    "Order " + order.getOrderNumber() + " placed successfully. Total: $" + order.getTotalAmount());

            System.out.println("Order placed: " + order);
            return Optional.of(order);
        }
    }

    public boolean cancelOrder(Member member, String orderNumber) {
        Optional<Order> orderOpt = orderRepo.getOrderById(orderNumber);
        if (!orderOpt.isPresent()) {
            System.out.println("Order not found: " + orderNumber);
            return false;
        }

        Order order = orderOpt.get();

        // BUG 7 FIX: Synchronize on the order object to prevent double-cancel race
        synchronized (order) {
            if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.COMPLETED
                    || order.getStatus() == OrderStatus.CANCELED) {
                System.out.println("Cannot cancel order " + orderNumber + " - already " + order.getStatus());
                return false;
            }

            order.cancel();
        }

        // Restore stock (safe outside lock - incrementStock is atomic via addAndGet)
        for (Item item : order.getItems()) {
            Optional<Product> product = productRepo.getProductById(item.getProductId());
            product.ifPresent(p -> p.incrementStock(item.getQuantity()));
        }

        notificationService.notifyObservers(NotificationType.ORDER_CONFIRMATION,
                "Order " + orderNumber + " has been canceled.");

        return true;
    }

    public ReturnRequest requestReturn(String orderNumber, String reason) {
        Optional<Order> orderOpt = orderRepo.getOrderById(orderNumber);
        if (!orderOpt.isPresent()) {
            System.out.println("Order not found: " + orderNumber);
            return null;
        }

        Order order = orderOpt.get();

        // Synchronize on order to prevent concurrent status transitions
        synchronized (order) {
            if (order.getStatus() != OrderStatus.COMPLETED) {
                System.out.println("Can only return completed orders. Current status: " + order.getStatus());
                return null;
            }

            order.setStatus(OrderStatus.RETURN_REQUESTED);
        }

        ReturnRequest returnRequest = new ReturnRequest(orderNumber, reason, order.getTotalAmount());
        order.addOrderLog(new OrderLog(orderNumber, OrderStatus.RETURN_REQUESTED, "Return requested: " + reason));
        notificationService.notifyObservers(NotificationType.REFUND_STATUS,
                "Return requested for order " + orderNumber);

        System.out.println("Return request created: " + returnRequest);
        return returnRequest;
    }

    public Refund processRefund(ReturnRequest returnRequest) {
        returnRequest.approve();
        Refund refund = new Refund(returnRequest.getReturnId(), returnRequest.getRefundAmount());
        refund.processRefund();

        Optional<Order> orderOpt = orderRepo.getOrderById(returnRequest.getOrderId());
        orderOpt.ifPresent(order -> {
            order.setStatus(OrderStatus.REFUND_APPLIED);
            order.setPaymentStatus(PaymentStatus.REFUNDED);
            order.addOrderLog(new OrderLog(order.getOrderNumber(), OrderStatus.REFUND_APPLIED,
                    "Refund of $" + refund.getAmount() + " processed."));

            // Restore stock
            for (Item item : order.getItems()) {
                Optional<Product> product = productRepo.getProductById(item.getProductId());
                product.ifPresent(p -> p.incrementStock(item.getQuantity()));
            }
        });

        returnRequest.setStatus(ReturnStatus.REFUNDED);
        notificationService.notifyObservers(NotificationType.REFUND_STATUS,
                "Refund of $" + refund.getAmount() + " has been processed.");

        return refund;
    }

    public Optional<Order> getOrder(String orderNumber) {
        return orderRepo.getOrderById(orderNumber);
    }

    public List<Order> getAllOrders() {
        return orderRepo.getAllOrders();
    }

    /**
     * Rollback stock for items that were already decremented when a later item fails.
     */
    private void rollbackStock(List<Item> decrementedItems) {
        for (Item item : decrementedItems) {
            Optional<Product> product = productRepo.getProductById(item.getProductId());
            product.ifPresent(p -> p.incrementStock(item.getQuantity()));
        }
    }
}
