package org.example.services;

import org.example.enums.TransactionType;
import org.example.factory.TransactionFactory;
import org.example.hardware.CardReader;
import org.example.hardware.CashDispenser;
import org.example.hardware.DepositSlot;
import org.example.hardware.Keypad;
import org.example.hardware.PrinterManager;
import org.example.hardware.Screen;
import org.example.models.BankResponse;
import org.example.models.Card;
import org.example.models.Customer;
import org.example.observer.NotificationService;
import org.example.repository.AccountRepository;
import org.example.repository.SessionManager;
import org.example.strategy.accountStrategy.AccountStrategy;
import org.example.transaction.Transaction;

import java.util.Optional;

public class ATM {
    private final CashDispenser cashDispenser;
    private final DepositSlot depositSlot;
    private final Screen screen;
    private final Keypad keypad;
    private final CardReader cardReader;
    private final PrinterManager printerManager;
    private final AccountRepository accountRepo;
    private final SessionManager sessionManager;
    private final NotificationService notificationService;
    private final BankCommunicationService bankService;

    public ATM(CashDispenser cashDispenser, DepositSlot depositSlot, Screen screen,
               Keypad keypad, CardReader cardReader, PrinterManager printerManager,
               AccountRepository accountRepo, SessionManager sessionManager,
               NotificationService notificationService, BankCommunicationService bankService) {
        this.cashDispenser = cashDispenser;
        this.depositSlot = depositSlot;
        this.screen = screen;
        this.keypad = keypad;
        this.cardReader = cardReader;
        this.printerManager = printerManager;
        this.accountRepo = accountRepo;
        this.sessionManager = sessionManager;
        this.notificationService = notificationService;
        this.bankService = bankService;
    }

    public String authenticateUser(Card card, int pin) {
        Card readCard = cardReader.readCard(card);
        if (readCard == null) {
            screen.showError("Card could not be read.");
            return null;
        }

        if (!readCard.validatePin(pin)) {
            screen.showError("Invalid PIN.");
            return null;
        }

        Optional<AccountStrategy> accountOpt = accountRepo.getAccountByCard(readCard.getCardNumber());
        if (!accountOpt.isPresent()) {
            screen.showError("No account linked to this card.");
            cardReader.ejectCard();
            return null;
        }

        String sessionId = sessionManager.createSession(readCard.getCustomerName(), accountOpt.get());
        screen.showSuccess("Authentication successful. Welcome, " + readCard.getCustomerName() + "!");
        screen.showMainMenu();
        return sessionId;
    }

    public void executeTransaction(String sessionId, TransactionType type, double amount) {
        Optional<org.example.models.UserSession> sessionOpt = sessionManager.getSession(sessionId);
        if (!sessionOpt.isPresent()) {
            screen.showError("Session expired or invalid. Please re-authenticate.");
            return;
        }

        AccountStrategy account = sessionOpt.get().getAccount();
        Transaction transaction = TransactionFactory.createTransaction(type, account, amount,
                cashDispenser, depositSlot);

        screen.display("Processing " + type + "...");
        BankResponse response = bankService.sendTransaction(transaction);

        if (response.isSuccess()) {
            screen.showSuccess(response.getMessage());
            printerManager.printReceipt(type + " - Amount: $" + amount +
                    " - Account: " + account.getAccountNumber());
            notificationService.notifyObservers(type + " of $" + amount + " completed on account " +
                    account.getAccountNumber());
        } else {
            screen.showError(response.getMessage());
        }
    }

    public void executeTransfer(String sessionId, AccountStrategy destinationAccount, double amount) {
        Optional<org.example.models.UserSession> sessionOpt = sessionManager.getSession(sessionId);
        if (!sessionOpt.isPresent()) {
            screen.showError("Session expired or invalid. Please re-authenticate.");
            return;
        }

        AccountStrategy sourceAccount = sessionOpt.get().getAccount();
        Transaction transaction = TransactionFactory.createTransferTransaction(sourceAccount,
                destinationAccount, amount);

        screen.display("Processing TRANSFER...");
        BankResponse response = bankService.sendTransaction(transaction);

        if (response.isSuccess()) {
            screen.showSuccess(response.getMessage());
            printerManager.printReceipt("TRANSFER - $" + amount + " from account " +
                    sourceAccount.getAccountNumber() + " to account " +
                    destinationAccount.getAccountNumber());
            notificationService.notifyObservers("Transfer of $" + amount + " from account " +
                    sourceAccount.getAccountNumber() + " to account " +
                    destinationAccount.getAccountNumber());
        } else {
            screen.showError(response.getMessage());
        }
    }

    public void endSession(String sessionId) {
        sessionManager.invalidateSession(sessionId);
        cardReader.ejectCard();
        screen.showGoodbye();
    }

    public void shutdown() {
        bankService.shutdown();
        printerManager.shutdown();
    }

    // Getters for hardware components
    public CashDispenser getCashDispenser() { return cashDispenser; }
    public Screen getScreen() { return screen; }
}
