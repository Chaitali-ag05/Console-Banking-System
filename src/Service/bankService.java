package Service;

import domain.Account;
import domain.Transaction;

import java.nio.channels.AcceptPendingException;
import java.util.List;

public interface bankService {
    void deposit(String accountNumber, Double amount, String deposite);
    void withdraw(String accountNumber,Double amount,String withdraw);
    void transfer(String from , String to, Double amount, String transfer );
    List<Transaction> getStatement(String account);
    String openAccount(String Name, String Email, String accountType);
    List<Account> listAccount();
    List<Account> searchAccountByCustomerName(String q);
}
