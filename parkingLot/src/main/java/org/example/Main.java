package org.example;

import org.example.enums.ParkingSpotType;
import org.example.exceptions.ParkingFullException;
import org.example.factory.ParkingSpotFactory;
import org.example.model.*;
import org.example.services.ParkingLot;
import org.example.strategy.ParkingSpotObserver.ParkingDisplayBoard;
import org.example.strategy.command.Command;
import org.example.strategy.command.ProcessTicketCommand;
import org.example.strategy.paymentStrategy.CashPaymentStrategy;
import org.example.strategy.paymentStrategy.PaymentStrategy;

public class Main {
    public static void main(String[] args) {
        // Initialize parking lot
        ParkingLot parkingLot = ParkingLot.getInstance();

        // Create floors and spots
        ParkingFloor floor1 = new ParkingFloor("Floor 1");
        ParkingDisplayBoard displayBoard = new ParkingDisplayBoard("DB-1");

        // Add parking spots
        ParkingSpot compactSpot1 = ParkingSpotFactory.createParkingSpot(ParkingSpotType.COMPACT, "C-101");
        compactSpot1.setObserver(displayBoard);
        floor1.addSpot(compactSpot1);

        ParkingSpot largeSpot1 = ParkingSpotFactory.createParkingSpot(ParkingSpotType.LARGE, "L-101");
        largeSpot1.setObserver(displayBoard);
        floor1.addSpot(largeSpot1);

        parkingLot.addParkingFloor(floor1);

        // Create vehicle
        VehicleFactory carFactory = new CarFactory();
        Vehicle car = carFactory.createVehicle();

        // Get parking ticket
        try {
            ParkingTicket ticket = parkingLot.getNewParkingTicket(car);
            System.out.println("Parking ticket issued: " + ticket.getTicketNumber());

            // Process ticket
            ParkingAttendant attendant = new ParkingAttendant();
            Command processCommand = new ProcessTicketCommand(attendant, ticket.getTicketNumber());
            processCommand.execute();

            // Calculate fee
            double fee = ticket.getTotalFee(3);
            System.out.println("Total parking fee for 3 hours: $" + fee);

            // Pay for ticket
            PaymentStrategy paymentStrategy = new CashPaymentStrategy();
            paymentStrategy.pay(ticket);

            // Check if can exit
            System.out.println("Can exit: " + ticket.canExit());

        } catch (ParkingFullException e) {
            System.out.println(e.getMessage());
        }
    }
}