package repository;

import domain.Transaction;

import java.util.*;

public class TransactionRepository {
    private final Map<String, List<Transaction>> txtByAccount = new HashMap<>();

    public void add(Transaction trasaction) {
          List<Transaction> list =txtByAccount.computeIfAbsent(trasaction.getAccountNumber(),
                  k-> new ArrayList<>());
          list.add(trasaction);
    }

    public List<Transaction> findByAccount(String accountNumber) {
        return new ArrayList<>(txtByAccount.getOrDefault(accountNumber,Collections.emptyList()));
    }
}
