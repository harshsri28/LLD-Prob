package org.example.model;

import org.example.enums.ParkingTicketStatus;
import org.example.strategy.parkingFee.ParkingFeeStrategy;
import org.example.strategy.ticketState.ActiveTicketState;
import org.example.strategy.ticketState.PaidTicketState;
import org.example.strategy.ticketState.TicketState;

public class ParkingTicket {
    private String ticketNumber;
    private ParkingTicketStatus status;
    private TicketState state;
    private ParkingFeeStrategy feeStrategy;
    private static int ticketCounter = 0;

    public ParkingTicket(ParkingFeeStrategy feeStrategy) {
        this.ticketNumber = "TKT-" + (++ticketCounter);
        this.status = ParkingTicketStatus.ACTIVE;
        this.state = new ActiveTicketState();
        this.feeStrategy = feeStrategy;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void saveInDB() {
        System.out.println("Ticket saved to database: " + ticketNumber);
    }

    public void updateStatus(ParkingTicketStatus status) {
        this.status = status;
        if (status == ParkingTicketStatus.PAID) {
            this.state = new PaidTicketState();
        }
    }

    public void setState(TicketState state) {
        this.state = state;
    }

    public boolean canExit() {
        return state.canExit();
    }

    public double getTotalFee(int hours) {
        return feeStrategy.calculateFee(hours);
    }
}
