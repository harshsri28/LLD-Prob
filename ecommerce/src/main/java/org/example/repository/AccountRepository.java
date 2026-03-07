package org.example.repository;

import org.example.models.Account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AccountRepository {
    private Map<String, Account> accounts = new HashMap<>();

    public void addAccount(Account account) {
        accounts.put(account.getUserName(), account);
    }

    public Optional<Account> getAccountByUserName(String userName) {
        return Optional.ofNullable(accounts.get(userName));
    }

    public void removeAccount(String userName) {
        accounts.remove(userName);
    }

    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }
}
