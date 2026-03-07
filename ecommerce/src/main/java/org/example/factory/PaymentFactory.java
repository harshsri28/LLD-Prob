package org.example.factory;

import org.example.strategy.paymentStrategy.BankTransferPaymentStrategy;
import org.example.strategy.paymentStrategy.CreditCardPaymentStrategy;
import org.example.strategy.paymentStrategy.PaymentStrategy;

public class PaymentFactory {

    public static PaymentStrategy getPaymentStrategy(String paymentType) {
        switch (paymentType.toUpperCase()) {
            case "CREDIT_CARD":
                return new CreditCardPaymentStrategy();
            case "BANK_TRANSFER":
                return new BankTransferPaymentStrategy();
            default:
                throw new IllegalArgumentException("Unknown payment type: " + paymentType);
        }
    }
}
