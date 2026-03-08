package org.example.repository;

import org.example.strategy.accountStrategy.AccountStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class AccountRepository {
    private Map<Integer, AccountStrategy> accounts = new ConcurrentHashMap<>();
    private Map<String, Integer> cardToAccountMap = new ConcurrentHashMap<>();

    public void addAccount(AccountStrategy account) {
        accounts.put(account.getAccountNumber(), account);
    }

    public void linkCardToAccount(String cardNumber, int accountNumber) {
        cardToAccountMap.put(cardNumber, accountNumber);
    }

    public Optional<AccountStrategy> getAccountByNumber(int accountNumber) {
        return Optional.ofNullable(accounts.get(accountNumber));
    }

    public Optional<AccountStrategy> getAccountByCard(String cardNumber) {
        Integer accountNumber = cardToAccountMap.get(cardNumber);
        if (accountNumber == null) return Optional.empty();
        return getAccountByNumber(accountNumber);
    }

    public List<AccountStrategy> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }
}
