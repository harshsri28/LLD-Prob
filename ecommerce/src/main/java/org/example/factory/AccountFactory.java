package org.example.factory;

import org.example.models.Account;
import org.example.models.Address;
import org.example.models.GuestAccount;
import org.example.models.Member;

public class AccountFactory {

    public static Account createAccount(String type, String userName, String password, String name, Address address) {
        switch (type.toUpperCase()) {
            case "MEMBER":
                return new Member(userName, password, name, address);
            case "GUEST":
                return new GuestAccount(userName, password, name, address);
            default:
                throw new IllegalArgumentException("Unknown account type: " + type);
        }
    }
}
