package org.example.repository;

import org.example.models.Bank;
import java.util.HashMap;
import java.util.Map;

public class BankRepository {
    private Map<String, Bank> banks = new HashMap<>();

    public void save(Bank bank) {
        banks.put(bank.getId(), bank);
    }

    public Bank findById(String id) {
        return banks.get(id);
    }

    public Map<String, Bank> findAll() {
        return banks;
    }
}
