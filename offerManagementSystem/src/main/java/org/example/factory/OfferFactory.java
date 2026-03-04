package org.example.factory;

import org.example.models.Offer;
import org.example.strategy.computation.*;

public class OfferFactory {
    public static Offer createFixedPercentageOffer(String id, String name, double percentage) {
        return new Offer(id, name, new FixedPercentageStrategy(percentage));
    }

    public static Offer createFixedAmountOffer(String id, String name, double amount) {
        return new Offer(id, name, new FixedAmountStrategy(amount));
    }

    public static Offer createCappedPercentageOffer(String id, String name, double percentage, double maxCap) {
        return new Offer(id, name, new CappedPercentageStrategy(percentage, maxCap));
    }
}
