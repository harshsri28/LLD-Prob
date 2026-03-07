package org.example.services;

import org.example.enums.NotificationType;
import org.example.enums.OrderStatus;
import org.example.enums.ShipmentStatus;
import org.example.models.Order;
import org.example.models.OrderLog;
import org.example.models.Shipment;
import org.example.observer.NotificationService;
import org.example.repository.OrderRepository;

import java.util.List;

public class ShipmentService {
    private OrderRepository orderRepo;
    private NotificationService notificationService;

    public ShipmentService(OrderRepository orderRepo, NotificationService notificationService) {
        this.orderRepo = orderRepo;
        this.notificationService = notificationService;
    }

    public void shipOrder(Order order) {
        if (order.getStatus() != OrderStatus.PENDING) {
            System.out.println("Cannot ship order " + order.getOrderNumber() + " - status: " + order.getStatus());
            return;
        }
        order.sendForShipment();
        notificationService.notifyObservers(NotificationType.SHIPMENT_UPDATE,
                "Order " + order.getOrderNumber() + " has been shipped. Tracking: " +
                        order.getShipment().getTrackingNumber());
    }

    public void updateShipmentStatus(Order order, ShipmentStatus newStatus) {
        Shipment shipment = order.getShipment();
        if (shipment == null) {
            System.out.println("No shipment found for order: " + order.getOrderNumber());
            return;
        }

        shipment.updateStatus(newStatus);
        order.addOrderLog(new OrderLog(order.getOrderNumber(), order.getStatus(),
                "Shipment status updated to: " + newStatus));

        if (newStatus == ShipmentStatus.DELIVERED) {
            order.setStatus(OrderStatus.COMPLETED);
        }

        notificationService.notifyObservers(NotificationType.SHIPMENT_UPDATE,
                "Order " + order.getOrderNumber() + " shipment status: " + newStatus);
    }

    public ShipmentStatus trackShipment(Order order) {
        Shipment shipment = order.getShipment();
        if (shipment == null) {
            System.out.println("No shipment found for order: " + order.getOrderNumber());
            return null;
        }
        System.out.println("Tracking order " + order.getOrderNumber() + ": " + shipment);
        return shipment.getStatus();
    }

    public void updateAllShipments() {
        List<Order> shippedOrders = orderRepo.findByStatus(OrderStatus.SHIPPED);
        for (Order order : shippedOrders) {
            // In a real system, this would call an external tracking API
            System.out.println("Checking shipment status for order: " + order.getOrderNumber());
        }
    }
}
