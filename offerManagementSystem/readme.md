# Offer Management System


## Problem Statement

You are tasked with designing a SaaS platform to manage offers on credit cards for banks. The platform should allow administrators to create and define various types of offers based on customer eligibility and computational strategies. Offers will be linked to transactions, and the system should compute benefits for eligible transactions. Consider the given constraints and design an efficient and scalable Offer Management System.

## Requirements:

1. Banks can create and manage offers on their credit cards.
2. Offers have customer eligibility criteria and computational strategies for cashback/rewards.
3. Customer eligibility is defined based on various fields such as age, date of birth, gender, etc.
4. Transactional eligibility can be based on transaction amount etc.
5. Offers should support computation strategies including fixed amount, fixed percentage, and fixed percentage with a maximum cap.
6. Transactions will have fields: `merchant_name`, `merchant_id`, `transaction_date`, `city`, `amount`
7. Customers will have fields: `age`, `dob`, `gender`.
8. Eligibility can be defined on all transaction and customer fields.
9. The system should compute benefits for transactions based on eligible offers.
10. Example Offers:
    - Flat 1% cashback on all transactions.
    - 2% cashback up to 500 on transactions greater than 1000 made in Bangalore.


## Tasks:

1. Design the classes and relationships for the Offer Management System.
2. Implement data structures and algorithms to efficiently handle customer and transaction eligibility checks.
3. Define the computational strategies (fixed amount, fixed percentage, capped percentage) and implement the logic to calculate benefits.
4. Implement the mechanism for administrators to create and manage offers, including the example offers specified.
5. Implement the computation of benefits for transactions based on the eligible offers.

## Note:
- Focus on object-oriented principles, modularity, and reusability in your design.
- Consider using appropriate design patterns to solve specific problems within the system.
- Provide explanations for your design choices and any trade-offs made during the design process.
