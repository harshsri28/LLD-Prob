package org.example.models;

public class ElectronicBankTransfer {
    private String bankName;
    private String routingNumber;
    private String accountNumber;

    public ElectronicBankTransfer(String bankName, String routingNumber, String accountNumber) {
        this.bankName = bankName;
        this.routingNumber = routingNumber;
        this.accountNumber = accountNumber;
    }

    public String getBankName() { return bankName; }
    public String getRoutingNumber() { return routingNumber; }
    public String getAccountNumber() { return accountNumber; }

    @Override
    public String toString() {
        return "BankTransfer{bank='" + bankName + "', account=****" +
                accountNumber.substring(accountNumber.length() - 4) + "}";
    }
}
