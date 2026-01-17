package org.example.model;

import org.example.enums.AccountStatus;

public class Account {
    private String id;
    private String password;
    private AccountStatus status;

    public Account() {
        this.status = AccountStatus.ACTIVE;
    }

    public boolean resetPassword() {
        System.out.println("Password reset.");
        return true;
    }
}
