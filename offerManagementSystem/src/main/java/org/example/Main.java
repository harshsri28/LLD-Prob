package org.example;

import org.example.enums.Gender;
import org.example.factory.OfferFactory;
import org.example.models.*;
import org.example.repository.BankRepository;
import org.example.repository.CustomerRepository;
import org.example.services.BenefitService;
import org.example.strategy.computation.*;
import org.example.strategy.eligibility.*;

import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // 1. Initialize Repositories
        BankRepository bankRepository = new BankRepository();
        CustomerRepository customerRepository = new CustomerRepository();

        // 2. Initialize Services
        BenefitService benefitService = new BenefitService(bankRepository, customerRepository);

        // 3. Setup Data
        // Create Banks
        Bank hdfc = new Bank("bank1", "HDFC");
        bankRepository.save(hdfc);

        // Create Customer
        Customer customer = new Customer("cust1", 25, new Date(), Gender.MALE);
        customerRepository.save(customer);

        // 4. Create Offers
        // Offer 1: Flat 1% cashback on all transactions
        Offer flatOffer = OfferFactory.createFixedPercentageOffer("offer1", "Flat 1% Cashback", 1.0);
        hdfc.addOffer(flatOffer);

        // Offer 2: 2% cashback up to 500 on transactions greater than 1000 made in Bangalore
        Offer bangaloreOffer = OfferFactory.createCappedPercentageOffer("offer2", "2% Cashback Bangalore Special", 2.0, 500.0);
        bangaloreOffer.addTransactionCriteria(new AmountCriteria(1000.0));
        bangaloreOffer.addTransactionCriteria(new CityCriteria("Bangalore"));
        hdfc.addOffer(bangaloreOffer);

        // Offer 3: Flat 100 cashback for Amazon transactions
        Offer amazonOffer = OfferFactory.createFixedAmountOffer("offer3", "Amazon Flat 100", 100.0);
        amazonOffer.addTransactionCriteria(new MerchantCriteria("Amazon"));
        hdfc.addOffer(amazonOffer);

        // 5. Simulate Transactions
        System.out.println("--- Scenario 1: Transaction of 500 in Delhi on Amazon ---");
        Transaction t1 = new Transaction("tx1", "Amazon", "m1", new Date(), "Delhi", 500.0, "cust1");
        double benefit1 = benefitService.calculateTotalBenefits(t1);
        List<Offer> eligibleOffers1 = benefitService.getEligibleOffers(t1);
        printResults(t1, benefit1, eligibleOffers1);

        System.out.println("\n--- Scenario 2: Transaction of 2000 in Bangalore ---");
        Transaction t2 = new Transaction("tx2", "Flipkart", "m2", new Date(), "Bangalore", 2000.0, "cust1");
        double benefit2 = benefitService.calculateTotalBenefits(t2);
        List<Offer> eligibleOffers2 = benefitService.getEligibleOffers(t2);
        printResults(t2, benefit2, eligibleOffers2);

        System.out.println("\n--- Scenario 3: Transaction of 50000 in Bangalore (Max Cap Check) ---");
        Transaction t3 = new Transaction("tx3", "Apple Store", "m3", new Date(), "Bangalore", 50000.0, "cust1");
        double benefit3 = benefitService.calculateTotalBenefits(t3);
        List<Offer> eligibleOffers3 = benefitService.getEligibleOffers(t3);
        printResults(t3, benefit3, eligibleOffers3);
    }

    private static void printResults(Transaction tx, double totalBenefit, List<Offer> eligibleOffers) {
        System.out.println("Transaction Amount: " + tx.getAmount() + " in " + tx.getCity());
        System.out.println("Total Benefit Calculated: " + totalBenefit);
        System.out.println("Eligible Offers: ");
        for (Offer offer : eligibleOffers) {
            System.out.println(" - " + offer.getName());
        }
    }
}
