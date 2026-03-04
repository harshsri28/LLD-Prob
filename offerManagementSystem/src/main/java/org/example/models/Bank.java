package org.example.models;

import java.util.ArrayList;
import java.util.List;

public class Bank {
    private String id;
    private String name;
    private List<Offer> offers;

    public Bank(String id, String name) {
        this.id = id;
        this.name = name;
        this.offers = new ArrayList<>();
    }

    public void addOffer(Offer offer) {
        offers.add(offer);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public List<Offer> getOffers() { return offers; }
}
