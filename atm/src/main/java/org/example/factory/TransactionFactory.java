package org.example.factory;

import org.example.enums.TransactionType;
import org.example.hardware.CashDispenser;
import org.example.hardware.DepositSlot;
import org.example.strategy.accountStrategy.AccountStrategy;
import org.example.transaction.BalanceInquiry;
import org.example.transaction.Deposit;
import org.example.transaction.Transaction;
import org.example.transaction.Transfer;
import org.example.transaction.Withdraw;

public class TransactionFactory {

    public static Transaction createTransaction(TransactionType type, AccountStrategy account,
                                                 double amount, CashDispenser cashDispenser,
                                                 DepositSlot depositSlot) {
        switch (type) {
            case BALANCE_INQUIRY:
                return new BalanceInquiry(account);
            case DEPOSIT_CASH:
            case DEPOSIT_CHECK:
                return new Deposit(account, amount, depositSlot);
            case WITHDRAW:
                return new Withdraw(account, amount, cashDispenser);
            default:
                throw new IllegalArgumentException("Invalid transaction type: " + type);
        }
    }

    public static Transaction createTransferTransaction(AccountStrategy source,
                                                         AccountStrategy destination,
                                                         double amount) {
        return new Transfer(source, destination, amount);
    }
}
