package org.example.models;

import org.example.observer.NotificationObserver;

public class Customer implements NotificationObserver {
    private String name;
    private String email;
    private String phone;
    private Address address;
    private Card card;

    public Customer(String name, String email, String phone, Address address, Card card) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.card = card;
    }

    @Override
    public void update(String message) {
        System.out.println("Notification for " + name + ": " + message);
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public Address getAddress() { return address; }
    public Card getCard() { return card; }

    @Override
    public String toString() {
        return "Customer{name='" + name + "', card=" + card + "}";
    }
}
