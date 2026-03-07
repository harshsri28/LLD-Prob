package org.example.models;

import org.example.enums.AccountStatus;
import org.example.enums.UserRole;

import java.util.ArrayList;
import java.util.List;

public class Account {
    private String userName;
    private String password;
    private AccountStatus status;
    private String name;
    private Address shippingAddress;
    private String email;
    private String phone;
    private UserRole role;
    private List<CreditCard> creditCards;
    private List<ElectronicBankTransfer> bankAccounts;

    public Account(String userName, String password, String name, Address shippingAddress, UserRole role) {
        this.userName = userName;
        this.password = password;
        this.name = name;
        this.shippingAddress = shippingAddress;
        this.role = role;
        this.status = AccountStatus.ACTIVE;
        this.creditCards = new ArrayList<>();
        this.bankAccounts = new ArrayList<>();
    }

    public void addCreditCard(CreditCard card) { creditCards.add(card); }
    public void addBankAccount(ElectronicBankTransfer bank) { bankAccounts.add(bank); }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public AccountStatus getStatus() { return status; }
    public void setStatus(AccountStatus status) { this.status = status; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Address getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(Address shippingAddress) { this.shippingAddress = shippingAddress; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
    public List<CreditCard> getCreditCards() { return creditCards; }
    public List<ElectronicBankTransfer> getBankAccounts() { return bankAccounts; }

    @Override
    public String toString() {
        return "Account{userName='" + userName + "', name='" + name + "', role=" + role + "}";
    }
}
