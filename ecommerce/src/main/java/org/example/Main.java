package org.example;

import org.example.command.AddItemToCartCommand;
import org.example.command.Command;
import org.example.command.PlaceOrderCommand;
import org.example.command.RemoveItemFromCartCommand;
import org.example.enums.NotificationType;
import org.example.enums.ProductCategoryType;
import org.example.enums.ShipmentStatus;
import org.example.factory.AccountFactory;
import org.example.factory.PaymentFactory;
import org.example.models.Account;
import org.example.models.Address;
import org.example.models.GuestAccount;
import org.example.models.Item;
import org.example.models.Member;
import org.example.models.Order;
import org.example.models.Product;
import org.example.models.ProductCategory;
import org.example.models.Refund;
import org.example.models.ReturnRequest;
import org.example.models.ShoppingCart;
import org.example.observer.NotificationService;
import org.example.repository.AccountRepository;
import org.example.repository.OrderRepository;
import org.example.repository.ProductRepository;
import org.example.repository.ReviewRepository;
import org.example.services.OrderService;
import org.example.services.ProductService;
import org.example.services.ShipmentService;
import org.example.strategy.paymentStrategy.PaymentStrategy;
import org.example.strategy.searchStrategy.CategorySearchStrategy;
import org.example.strategy.searchStrategy.NameSearchStrategy;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Amazon E-Commerce System Demo ===\n");

        // ========================================
        // 1. Initialize Repositories
        // ========================================
        AccountRepository accountRepo = new AccountRepository();
        ProductRepository productRepo = new ProductRepository();
        OrderRepository orderRepo = new OrderRepository();
        ReviewRepository reviewRepo = new ReviewRepository();

        // ========================================
        // 2. Initialize Observer (Notification System)
        // ========================================
        NotificationService notificationService = new NotificationService();

        // ========================================
        // 3. Initialize Services (Singleton for OrderService)
        // ========================================
        ProductService productService = new ProductService(productRepo, reviewRepo, new NameSearchStrategy());
        OrderService orderService = OrderService.getInstance(orderRepo, productRepo, notificationService);
        ShipmentService shipmentService = new ShipmentService(orderRepo, notificationService);

        // ========================================
        // 4. Create Accounts using Factory Pattern
        // ========================================
        System.out.println("--- Creating Accounts ---");
        Address aliceAddress = new Address("123 Main St", "Seattle", "WA", "98101", "USA");
        Address bobAddress = new Address("456 Oak Ave", "Portland", "OR", "97201", "USA");

        Member alice = (Member) AccountFactory.createAccount("MEMBER", "alice123", "pass123", "Alice Johnson", aliceAddress);
        Member bob = (Member) AccountFactory.createAccount("MEMBER", "bob456", "pass456", "Bob Smith", bobAddress);
        GuestAccount guest = (GuestAccount) AccountFactory.createAccount("GUEST", "guest1", "guest", "Guest User", new Address());

        accountRepo.addAccount(alice);
        accountRepo.addAccount(bob);
        accountRepo.addAccount(guest);

        // Register observers for notifications
        notificationService.addObserver(alice);
        notificationService.addObserver(bob);

        System.out.println("Created accounts: " + alice + ", " + bob + ", " + guest);

        // ========================================
        // 5. Add Products (Sellers can add products)
        // ========================================
        System.out.println("\n--- Adding Products ---");
        ProductCategory electronics = new ProductCategory("Electronics", "Electronic devices", ProductCategoryType.ELECTRONICS);
        ProductCategory books = new ProductCategory("Books", "Physical and digital books", ProductCategoryType.BOOKS);
        ProductCategory clothing = new ProductCategory("Clothing", "Apparel and fashion", ProductCategoryType.CLOTHING);

        Product smartphone = productService.addProduct("P001", "Smartphone Pro", "Latest model smartphone", 699.99, electronics, alice, 10);
        Product laptop = productService.addProduct("P002", "Laptop Ultra", "High-performance laptop", 1299.99, electronics, alice, 5);
        Product javaBook = productService.addProduct("P003", "Java Design Patterns", "Comprehensive design patterns book", 49.99, books, bob, 20);
        Product tshirt = productService.addProduct("P004", "Cotton T-Shirt", "Comfortable cotton t-shirt", 19.99, clothing, bob, 50);

        // ========================================
        // 6. Search Products (by name and category)
        // ========================================
        System.out.println("\n--- Searching Products ---");

        // Search by name
        productService.setSearchStrategy(new NameSearchStrategy());
        List<Product> nameResults = productService.searchProducts("Smart");
        System.out.println("Search by name 'Smart': " + nameResults);

        // Search by category
        productService.setSearchStrategy(new CategorySearchStrategy());
        List<Product> categoryResults = productService.searchProducts("ELECTRONICS");
        System.out.println("Search by category 'ELECTRONICS': " + categoryResults);

        // ========================================
        // 7. Guest tries to buy -> must register first
        // ========================================
        System.out.println("\n--- Guest Registration ---");
        System.out.println("Guest trying to browse: " + productService.getAllProducts().size() + " products available.");
        guest.registerAccount();

        // ========================================
        // 8. Shopping Cart (Command Pattern with optimistic locking)
        // ========================================
        System.out.println("\n--- Shopping Cart Operations (Command Pattern) ---");
        ShoppingCart aliceCart = alice.getCart();

        // Add items using Command Pattern
        Command addSmartphone = new AddItemToCartCommand(aliceCart, new Item("P001", 1, 699.99));
        Command addBook = new AddItemToCartCommand(aliceCart, new Item("P003", 2, 49.99));
        Command addTshirt = new AddItemToCartCommand(aliceCart, new Item("P004", 3, 19.99));

        addSmartphone.execute();
        addBook.execute();
        addTshirt.execute();

        System.out.println("Alice's cart: " + aliceCart);

        // Modify quantity
        aliceCart.updateItemQuantity("P004", 1);
        System.out.println("After updating t-shirt quantity: " + aliceCart);

        // Remove item
        Command removeTshirt = new RemoveItemFromCartCommand(aliceCart, "P004");
        removeTshirt.execute();
        System.out.println("After removing t-shirt: " + aliceCart);

        // ========================================
        // 9. Checkout & Place Order (Strategy Pattern for payment)
        // ========================================
        System.out.println("\n--- Placing Order (Credit Card Payment) ---");
        PaymentStrategy creditCardPayment = PaymentFactory.getPaymentStrategy("CREDIT_CARD");
        Optional<Order> aliceOrder = orderService.placeOrder(alice, creditCardPayment);

        // ========================================
        // 10. Bob places an order with Bank Transfer
        // ========================================
        System.out.println("\n--- Bob's Order (Bank Transfer Payment) ---");
        ShoppingCart bobCart = bob.getCart();
        bobCart.addItem(new Item("P002", 1, 1299.99));

        PaymentStrategy bankTransfer = PaymentFactory.getPaymentStrategy("BANK_TRANSFER");
        Optional<Order> bobOrder = orderService.placeOrder(bob, bankTransfer);

        // ========================================
        // 11. Rate and Review Products
        // ========================================
        System.out.println("\n--- Product Reviews ---");
        productService.addReview("P001", "alice123", 5, "Amazing phone! Great camera and battery.");
        productService.addReview("P001", "bob456", 4, "Good phone, slightly overpriced.");
        productService.addReview("P003", "alice123", 5, "Must-read for Java developers.");

        System.out.println("Smartphone average rating: " + smartphone.getAverageRating() + "/5");
        System.out.println("Smartphone reviews: " + smartphone.getReviews());

        // ========================================
        // 12. Shipment & Tracking
        // ========================================
        System.out.println("\n--- Shipment & Tracking ---");
        if (aliceOrder.isPresent()) {
            Order order = aliceOrder.get();
            shipmentService.shipOrder(order);
            shipmentService.trackShipment(order);

            // Update shipment status
            shipmentService.updateShipmentStatus(order, ShipmentStatus.IN_TRANSIT);
            shipmentService.updateShipmentStatus(order, ShipmentStatus.OUT_FOR_DELIVERY);
            shipmentService.updateShipmentStatus(order, ShipmentStatus.DELIVERED);

            System.out.println("Final order status: " + order.getStatus());
        }

        // ========================================
        // 13. Cancel Order (only if not shipped)
        // ========================================
        System.out.println("\n--- Cancel Order ---");
        if (bobOrder.isPresent()) {
            Order order = bobOrder.get();
            boolean canceled = orderService.cancelOrder(bob, order.getOrderNumber());
            System.out.println("Bob's order canceled: " + canceled);
        }

        // ========================================
        // 14. Return & Refund
        // ========================================
        System.out.println("\n--- Return & Refund ---");
        if (aliceOrder.isPresent()) {
            Order order = aliceOrder.get();
            ReturnRequest returnRequest = orderService.requestReturn(order.getOrderNumber(), "Product not as described");
            if (returnRequest != null) {
                Refund refund = orderService.processRefund(returnRequest);
                System.out.println("Refund processed: " + refund);
            }
        }

        // ========================================
        // 15. Concurrent Order Simulation (like carRental)
        // ========================================
        System.out.println("\n--- Concurrent Order Simulation ---");
        // Add a product with limited stock (only 1 unit)
        Product limitedProduct = productService.addProduct("P005", "Limited Edition Watch", "Only 1 available", 999.99,
                new ProductCategory("Accessories", "Watches", ProductCategoryType.OTHER), alice, 1);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Runnable aliceTask = () -> {
            System.out.println("Alice: Attempting to buy limited watch...");
            Member threadAlice = alice;
            threadAlice.getCart().addItem(new Item("P005", 1, 999.99));
            Optional<Order> result = orderService.placeOrder(threadAlice, creditCardPayment);
            if (result.isPresent()) {
                System.out.println("Alice: Order SUCCESS! " + result.get().getOrderNumber());
            } else {
                System.out.println("Alice: Order FAILED! (likely out of stock)");
            }
        };

        Runnable bobTask = () -> {
            System.out.println("Bob: Attempting to buy limited watch...");
            Member threadBob = bob;
            threadBob.getCart().addItem(new Item("P005", 1, 999.99));
            Optional<Order> result = orderService.placeOrder(threadBob, bankTransfer);
            if (result.isPresent()) {
                System.out.println("Bob: Order SUCCESS! " + result.get().getOrderNumber());
            } else {
                System.out.println("Bob: Order FAILED! (likely out of stock)");
            }
        };

        executor.submit(aliceTask);
        executor.submit(bobTask);

        executor.shutdown();
        if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
            System.out.println("Executor did not terminate in time.");
            executor.shutdownNow();
        }

        // ========================================
        // 16. Final Summary
        // ========================================
        System.out.println("\n=== Final Summary ===");
        System.out.println("Total orders: " + orderService.getAllOrders().size());
        System.out.println("Total products: " + productService.getAllProducts().size());
        System.out.println("Limited watch remaining stock: " + limitedProduct.getAvailableItemCount());
        System.out.println("Alice's orders: " + alice.getOrders().size());
        System.out.println("Bob's orders: " + bob.getOrders().size());
        System.out.println("\n=== Demo Complete ===");
    }
}
