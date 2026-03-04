package org.example.services;

import org.example.models.Bank;
import org.example.models.Customer;
import org.example.models.Offer;
import org.example.models.Transaction;
import org.example.repository.BankRepository;
import org.example.repository.CustomerRepository;

import java.util.ArrayList;
import java.util.List;

public class BenefitService {
    private BankRepository bankRepository;
    private CustomerRepository customerRepository;

    public BenefitService(BankRepository bankRepository, CustomerRepository customerRepository) {
        this.bankRepository = bankRepository;
        this.customerRepository = customerRepository;
    }

    public double calculateTotalBenefits(Transaction transaction) {
        Customer customer = customerRepository.findById(transaction.getCustomerId());
        if (customer == null) {
            return 0.0;
        }

        double totalBenefit = 0.0;
        for (Bank bank : bankRepository.findAll().values()) {
            for (Offer offer : bank.getOffers()) {
                if (offer.isEligible(customer, transaction)) {
                    totalBenefit += offer.calculateBenefit(transaction);
                }
            }
        }
        return totalBenefit;
    }

    public List<Offer> getEligibleOffers(Transaction transaction) {
        Customer customer = customerRepository.findById(transaction.getCustomerId());
        List<Offer> eligibleOffers = new ArrayList<>();
        if (customer == null) return eligibleOffers;

        for (Bank bank : bankRepository.findAll().values()) {
            for (Offer offer : bank.getOffers()) {
                if (offer.isEligible(customer, transaction)) {
                    eligibleOffers.add(offer);
                }
            }
        }
        return eligibleOffers;
    }
}
