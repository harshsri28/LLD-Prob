package org.example.models;

import java.util.Date;

public class Card {
    private String cardNumber;
    private String customerName;
    private Date expiryDate;
    private int pin;

    public Card(String cardNumber, String customerName, Date expiryDate, int pin) {
        this.cardNumber = cardNumber;
        this.customerName = customerName;
        this.expiryDate = expiryDate;
        this.pin = pin;
    }

    public boolean validatePin(int enteredPin) {
        return this.pin == enteredPin;
    }

    public boolean isExpired() {
        return new Date().after(expiryDate);
    }

    public String getCardNumber() { return cardNumber; }
    public String getCustomerName() { return customerName; }
    public Date getExpiryDate() { return expiryDate; }

    @Override
    public String toString() {
        return "Card{****" + cardNumber.substring(cardNumber.length() - 4) + ", " + customerName + "}";
    }
}
