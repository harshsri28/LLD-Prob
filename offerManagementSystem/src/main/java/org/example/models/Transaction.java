package org.example.models;

import java.util.Date;

public class Transaction {
    private String id;
    private String merchantName;
    private String merchantId;
    private Date transactionDate;
    private String city;
    private double amount;
    private String customerId;

    public Transaction(String id, String merchantName, String merchantId, Date transactionDate, String city, double amount, String customerId) {
        this.id = id;
        this.merchantName = merchantName;
        this.merchantId = merchantId;
        this.transactionDate = transactionDate;
        this.city = city;
        this.amount = amount;
        this.customerId = customerId;
    }

    public String getId() { return id; }
    public String getMerchantName() { return merchantName; }
    public String getMerchantId() { return merchantId; }
    public Date getTransactionDate() { return transactionDate; }
    public String getCity() { return city; }
    public double getAmount() { return amount; }
    public String getCustomerId() { return customerId; }
}
