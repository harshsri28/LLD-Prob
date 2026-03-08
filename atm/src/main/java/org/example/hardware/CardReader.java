package org.example.hardware;

import org.example.models.Card;

public class CardReader {

    public Card readCard(Card card) {
        if (card == null) {
            System.out.println("[CARD READER] No card inserted.");
            return null;
        }
        if (card.isExpired()) {
            System.out.println("[CARD READER] Card is expired: " + card);
            return null;
        }
        System.out.println("[CARD READER] Card read successfully: " + card);
        return card;
    }

    public void ejectCard() {
        System.out.println("[CARD READER] Card ejected.");
    }

    public void retainCard() {
        System.out.println("[CARD READER] Card retained by ATM.");
    }
}
