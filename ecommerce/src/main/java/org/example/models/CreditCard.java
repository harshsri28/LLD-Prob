package org.example.models;

public class CreditCard {
    private String cardNumber;
    private String nameOnCard;
    private String expiryDate;
    private int cvv;

    public CreditCard(String cardNumber, String nameOnCard, String expiryDate, int cvv) {
        this.cardNumber = cardNumber;
        this.nameOnCard = nameOnCard;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
    }

    public String getCardNumber() { return cardNumber; }
    public String getNameOnCard() { return nameOnCard; }
    public String getExpiryDate() { return expiryDate; }
    public int getCvv() { return cvv; }

    @Override
    public String toString() {
        return "CreditCard{****" + cardNumber.substring(cardNumber.length() - 4) + ", " + nameOnCard + "}";
    }
}
