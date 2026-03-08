package org.example.hardware;

public class Screen {

    public void display(String message) {
        System.out.println("[SCREEN] " + message);
    }

    public void showWelcome() {
        display("Welcome to the ATM. Please insert your card.");
    }

    public void showMainMenu() {
        display("=== Main Menu ===");
        display("1. Balance Inquiry");
        display("2. Cash Withdrawal");
        display("3. Cash Deposit");
        display("4. Transfer");
        display("5. Exit");
    }

    public void showError(String error) {
        display("ERROR: " + error);
    }

    public void showSuccess(String message) {
        display("SUCCESS: " + message);
    }

    public void showBalance(double balance) {
        display("Your current balance: $" + String.format("%.2f", balance));
    }

    public void showGoodbye() {
        display("Thank you for using the ATM. Goodbye!");
    }
}
