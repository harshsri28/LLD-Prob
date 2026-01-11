package org.example.model;

public class Address {
    String street;
    String city;
    String state;
    String zipCode;
    String country;

    public Address(String street, String city, String state, String zipCode, String country) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
    }
}
