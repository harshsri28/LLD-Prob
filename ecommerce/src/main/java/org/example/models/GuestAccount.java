package org.example.models;

import org.example.enums.UserRole;

public class GuestAccount extends Account {

    public GuestAccount(String userName, String password, String name, Address address) {
        super(userName, password, name, address, UserRole.GUEST);
    }

    public boolean registerAccount() {
        setRole(UserRole.MEMBER);
        System.out.println("Guest account registered as Member: " + getName());
        return true;
    }
}
