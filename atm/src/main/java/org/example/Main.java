package org.example;

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
import org.example.models.Address;
import org.example.observer.NotificationService;
import org.example.repository.AccountRepository;
import org.example.repository.SessionManager;
import org.example.services.ATM;
import org.example.services.AccountStatusManager;
import org.example.services.BankCommunicationService;
import org.example.strategy.accountStrategy.AccountStrategy;
import org.example.strategy.accountStrategy.CheckingAccount;
import org.example.strategy.accountStrategy.SavingAccount;
import org.example.transaction.Transaction;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== ATM System Demo ===\n");

        // ========================================
        // 1. Initialize Hardware Components
        // ========================================
        CashDispenser cashDispenser = new CashDispenser(10000.0);
        DepositSlot depositSlot = new DepositSlot();
        Screen screen = new Screen();
        Keypad keypad = new Keypad();
        CardReader cardReader = new CardReader();
        PrinterManager printerManager = new PrinterManager();

        // ========================================
        // 2. Initialize Repositories & Services
        // ========================================
        AccountRepository accountRepo = new AccountRepository();
        SessionManager sessionManager = new SessionManager(300000); // 5 min timeout
        NotificationService notificationService = new NotificationService();
        BankCommunicationService bankService = new BankCommunicationService(10, 5000);

        // ========================================
        // 3. Initialize ATM
        // ========================================
        ATM atm = new ATM(cashDispenser, depositSlot, screen, keypad, cardReader,
                printerManager, accountRepo, sessionManager, notificationService, bankService);

        // ========================================
        // 4. Create Accounts (Strategy Pattern)
        // ========================================
        System.out.println("--- Creating Accounts ---");
        CheckingAccount aliceChecking = new CheckingAccount(1001, 5000.0, "4111111111111111");
        SavingAccount aliceSaving = new SavingAccount(1002, 10000.0, 2000.0);
        CheckingAccount bobChecking = new CheckingAccount(2001, 3000.0, "4222222222222222");

        accountRepo.addAccount(aliceChecking);
        accountRepo.addAccount(aliceSaving);
        accountRepo.addAccount(bobChecking);

        // Link cards to accounts
        accountRepo.linkCardToAccount("4111111111111111", 1001);
        accountRepo.linkCardToAccount("4222222222222222", 2001);

        System.out.println("Accounts created: " + aliceChecking + ", " + aliceSaving + ", " + bobChecking);

        // ========================================
        // 5. Create Customers & Cards
        // ========================================
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 3);
        Date futureExpiry = cal.getTime();

        Card aliceCard = new Card("4111111111111111", "Alice Johnson", futureExpiry, 1234);
        Card bobCard = new Card("4222222222222222", "Bob Smith", futureExpiry, 5678);

        Customer alice = new Customer("Alice Johnson", "alice@email.com", "555-0101",
                new Address("123 Main St", "Seattle", "WA", "98101", "USA"), aliceCard);
        Customer bob = new Customer("Bob Smith", "bob@email.com", "555-0202",
                new Address("456 Oak Ave", "Portland", "OR", "97201", "USA"), bobCard);

        // Register observers for notifications
        notificationService.addObserver(alice);
        notificationService.addObserver(bob);

        // ========================================
        // 6. Authentication Flow
        // ========================================
        System.out.println("\n--- Authentication ---");
        screen.showWelcome();

        String aliceSession = atm.authenticateUser(aliceCard, 1234);
        System.out.println("Alice session: " + aliceSession);

        String bobSession = atm.authenticateUser(bobCard, 5678);
        System.out.println("Bob session: " + bobSession);

        // Wrong PIN test
        System.out.println("\n--- Wrong PIN Test ---");
        String failedSession = atm.authenticateUser(aliceCard, 9999);
        System.out.println("Failed session (expected null): " + failedSession);

        // ========================================
        // 7. Balance Inquiry (Template Method + Factory)
        // ========================================
        System.out.println("\n--- Balance Inquiry ---");
        atm.executeTransaction(aliceSession, TransactionType.BALANCE_INQUIRY, 0);

        // ========================================
        // 8. Cash Withdrawal
        // ========================================
        System.out.println("\n--- Cash Withdrawal ---");
        atm.executeTransaction(aliceSession, TransactionType.WITHDRAW, 500.0);
        System.out.println("Alice balance after withdrawal: $" + aliceChecking.getAvailableBalance());
        System.out.println("ATM cash remaining: $" + cashDispenser.getAvailableCash());

        // ========================================
        // 9. Cash Deposit
        // ========================================
        System.out.println("\n--- Cash Deposit ---");
        atm.executeTransaction(bobSession, TransactionType.DEPOSIT_CASH, 1000.0);
        System.out.println("Bob balance after deposit: $" + bobChecking.getAvailableBalance());

        // ========================================
        // 10. Fund Transfer (Deadlock-safe)
        // ========================================
        System.out.println("\n--- Fund Transfer ---");
        atm.executeTransfer(aliceSession, bobChecking, 200.0);
        System.out.println("Alice balance after transfer: $" + aliceChecking.getAvailableBalance());
        System.out.println("Bob balance after transfer: $" + bobChecking.getAvailableBalance());

        // ========================================
        // 11. Saving Account Withdrawal Limit Test
        // ========================================
        System.out.println("\n--- Saving Account Withdraw Limit ---");
        // Link Alice's saving account to a different card for testing
        String savingSession = sessionManager.createSession("Alice", aliceSaving);
        Transaction savingWithdraw = TransactionFactory.createTransaction(
                TransactionType.WITHDRAW, aliceSaving, 3000.0, cashDispenser, depositSlot);
        savingWithdraw.executeTransaction();
        System.out.println("Saving account withdraw of $3000 (limit $2000): status = " + savingWithdraw.getStatus());

        // Valid saving account withdrawal
        Transaction savingWithdrawOk = TransactionFactory.createTransaction(
                TransactionType.WITHDRAW, aliceSaving, 1000.0, cashDispenser, depositSlot);
        savingWithdrawOk.executeTransaction();
        System.out.println("Saving account withdraw of $1000: status = " + savingWithdrawOk.getStatus());
        System.out.println("Saving account balance: $" + aliceSaving.getAvailableBalance());

        // ========================================
        // 12. Account Status Management (AtomicReference)
        // ========================================
        System.out.println("\n--- Account Status Management ---");
        AccountStatusManager aliceStatus = new AccountStatusManager("alice-1001");
        System.out.println("Alice account active: " + aliceStatus.isActive());
        aliceStatus.blockAccount();
        System.out.println("Alice account active after block: " + aliceStatus.isActive());
        aliceStatus.unblockAccount();
        System.out.println("Alice account active after unblock: " + aliceStatus.isActive());

        // ========================================
        // 13. Concurrent Withdrawal Simulation
        //     (tests ReentrantLock + Semaphore)
        // ========================================
        System.out.println("\n--- Concurrent Withdrawal Simulation ---");
        System.out.println("Alice checking balance before concurrent test: $" + aliceChecking.getAvailableBalance());

        // Alice has ~$4300. Two threads try to withdraw $3000 each. Only one should succeed.
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Runnable withdraw1 = () -> {
            System.out.println("Thread 1: Attempting withdrawal of $3000...");
            Transaction t = TransactionFactory.createTransaction(
                    TransactionType.WITHDRAW, aliceChecking, 3000.0, cashDispenser, depositSlot);
            t.executeTransaction();
            System.out.println("Thread 1: Transaction status = " + t.getStatus());
        };

        Runnable withdraw2 = () -> {
            System.out.println("Thread 2: Attempting withdrawal of $3000...");
            Transaction t = TransactionFactory.createTransaction(
                    TransactionType.WITHDRAW, aliceChecking, 3000.0, cashDispenser, depositSlot);
            t.executeTransaction();
            System.out.println("Thread 2: Transaction status = " + t.getStatus());
        };

        executor.submit(withdraw1);
        executor.submit(withdraw2);

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println("Alice checking balance after concurrent test: $" + aliceChecking.getAvailableBalance());
        System.out.println("ATM cash remaining: $" + cashDispenser.getAvailableCash());

        // ========================================
        // 14. Concurrent Transfer Simulation
        //     (tests deadlock prevention via ordered locking)
        // ========================================
        System.out.println("\n--- Concurrent Transfer (Deadlock Prevention) ---");
        // Reset balances for clarity
        CheckingAccount acc1 = new CheckingAccount(3001, 5000.0, "5111111111111111");
        CheckingAccount acc2 = new CheckingAccount(3002, 5000.0, "5222222222222222");
        accountRepo.addAccount(acc1);
        accountRepo.addAccount(acc2);

        ExecutorService transferExecutor = Executors.newFixedThreadPool(2);

        // Thread 1: Transfer 1000 from acc1 -> acc2
        // Thread 2: Transfer 1000 from acc2 -> acc1
        // Without ordered locking, this would deadlock!
        Runnable transfer1 = () -> {
            System.out.println("Transfer 1: $1000 from acc1(3001) -> acc2(3002)...");
            Transaction t = TransactionFactory.createTransferTransaction(acc1, acc2, 1000.0);
            t.executeTransaction();
            System.out.println("Transfer 1: status = " + t.getStatus());
        };

        Runnable transfer2 = () -> {
            System.out.println("Transfer 2: $1000 from acc2(3002) -> acc1(3001)...");
            Transaction t = TransactionFactory.createTransferTransaction(acc2, acc1, 1000.0);
            t.executeTransaction();
            System.out.println("Transfer 2: status = " + t.getStatus());
        };

        transferExecutor.submit(transfer1);
        transferExecutor.submit(transfer2);

        transferExecutor.shutdown();
        transferExecutor.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println("Acc1 balance: $" + acc1.getAvailableBalance() + " (should be $5000)");
        System.out.println("Acc2 balance: $" + acc2.getAvailableBalance() + " (should be $5000)");

        // ========================================
        // 15. End Sessions
        // ========================================
        System.out.println("\n--- End Sessions ---");
        atm.endSession(aliceSession);
        atm.endSession(bobSession);

        // ========================================
        // 16. Final Summary
        // ========================================
        System.out.println("\n=== Final Summary ===");
        System.out.println("ATM cash remaining: $" + cashDispenser.getAvailableCash());
        System.out.println("Active sessions: " + sessionManager.getActiveSessionCount());

        // Shutdown
        atm.shutdown();

        // Give printer thread time to finish
        Thread.sleep(500);
        System.out.println("\n=== Demo Complete ===");
    }
}
