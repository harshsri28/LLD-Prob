package org.example.factory;

import org.example.enums.PaymentType;
import org.example.strategy.payment.CardPaymentStrategy;
import org.example.strategy.payment.PaymentStrategy;
import org.example.strategy.payment.UpiPaymentStrategy;

public class PaymentStrategyFactory {
    public static PaymentStrategy getPaymentStrategy(PaymentType paymentType){
        switch (paymentType){
            case UPI:
                return new UpiPaymentStrategy();
            case CREDIT_CARD:
                return new CardPaymentStrategy();
            default:
                return null;
        }
    }
}
