package org.example.command;

import org.example.models.Member;
import org.example.models.Order;

public class PlaceOrderCommand implements Command {
    private Member member;
    private Order order;

    public PlaceOrderCommand(Member member, Order order) {
        this.member = member;
        this.order = order;
    }

    @Override
    public void execute() {
        member.placeOrder(order);
    }
}
