package org.example.model;

public abstract class Person {
    private String name;
    private Address address;
    private String email;
    private String phone;
    private Account account;

    public Person(String name, Account account) {
        this.name = name;
        this.account = account;
    }

    public String getName() {
        return name;
    }
}
