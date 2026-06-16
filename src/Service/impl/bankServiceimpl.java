package Service.impl;

import Service.bankService;
import domain.Account;
import domain.Customer;
import domain.Transaction;
import domain.Type;
import exceptions.AccountNotFoundException;
import exceptions.InsufficientFundsException;
import exceptions.ValidationException;
import repository.AccountRepository;
import repository.CustomerRepository;
import repository.TransactionRepository;
import util.Validation;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public  class bankServiceimpl implements bankService {

     private  final AccountRepository accountrepository = new AccountRepository();
     private  final TransactionRepository transactionrepository = new TransactionRepository();
     private  final CustomerRepository customerrepository = new CustomerRepository();

     private  final Validation<String> ValidateName = name ->{
             if(name==null || name.isBlank()) throw  new ValidationException("Name not found");};

    private  final Validation<String> ValidateEmail = email ->{
        if(email==null || !email.contains("@")) throw  new ValidationException("Wrong Email");};

    private  final Validation<String> Validatetype = type ->{
        if(type==null || !(type.equalsIgnoreCase("SAVINGS")|| type.equalsIgnoreCase("CURRENT")))throw  new ValidationException("Type must be Savings or Current");};

    private  final Validation<Double> ValidateAmount = amount ->{
        if(amount==null || amount<0) throw new ValidationException("Please enter valid amount");};



        @Override
    public String openAccount(String Name, String Email, String accountType) {
        ValidateName.validate(Name);
        ValidateEmail.validate(Email);
        Validatetype.validate(accountType);
        String customerId = UUID.randomUUID().toString();
        Customer c= new Customer(customerId,Name,Email);
        String accountNumber = getAccountNumber();
        Account account = new Account(accountNumber,accountType,(double)0,customerId);
        //SAVE
            customerrepository.save(c);
            accountrepository.save(account);
        return accountNumber;
    }


    @Override
    public void deposit(String accountNumber, Double amount, String note) {
       ValidateAmount.validate(amount);
        Account account = (Account) accountrepository.findByNumber(accountNumber)
                     .orElseThrow(()-> new AccountNotFoundException("Account Not Found "+accountNumber));
        account.setBalance(account.getBalance()+amount);
        Transaction trasaction = new Transaction(account.getAccountNumber(),amount,UUID.randomUUID().toString(),note, LocalDateTime.now(), Type.DEPOSIT);
        transactionrepository.add(trasaction);
    }

    @Override
    public void withdraw(String accountNumber, Double amount, String note) {
        Account account = (Account) accountrepository.findByNumber(accountNumber)
                .orElseThrow(()-> new AccountNotFoundException("Account Not Found "+accountNumber));
       if(account.getBalance().compareTo(amount)<0)
           throw new InsufficientFundsException("Insufficient Balance");
        account.setBalance(account.getBalance()-amount);
        Transaction trasaction = new Transaction(account.getAccountNumber(),amount,UUID.randomUUID().toString(),note, LocalDateTime.now(), Type.WITHDRAW);
        transactionrepository.add(trasaction);
    }

    @Override
    public void transfer(String from, String to, Double amount, String note) {
        ValidateAmount.validate(amount);
            if(from.equals(to))
            throw new ValidationException(" Cannot transfer to your own account");
        Account account = (Account) accountrepository.findByNumber(from)
                .orElseThrow(()-> new AccountNotFoundException("Account Not Found "+from));
        Account accountto = (Account) accountrepository.findByNumber(to)
                .orElseThrow(()-> new AccountNotFoundException("Account Not Found "+to));

        if(account.getBalance().compareTo(amount)<0)
            throw new InsufficientFundsException("Insufficient Balance");

        account.setBalance(account.getBalance()-amount);
        accountto.setBalance(accountto.getBalance()+amount);
        Transaction fromtrasaction = new Transaction(account.getAccountNumber(),amount,UUID.randomUUID().toString(),note, LocalDateTime.now(), Type.TRANSFER_OUT);
        transactionrepository.add(fromtrasaction);
        Transaction totrasaction = new Transaction(accountto.getAccountNumber(),amount,UUID.randomUUID().toString(),note, LocalDateTime.now(), Type.TRANSFER_IN);
        transactionrepository.add(totrasaction);

    }

    @Override
    public List<Transaction> getStatement(String account) {
        return transactionrepository.findByAccount(account).stream()
                .sorted(Comparator.comparing(Transaction::getTimestamp))
                .collect(Collectors.toList());
    }

    @Override
    public List<Account> listAccount() {
        return accountrepository.findAll().stream()
                .sorted(Comparator.comparing(Account::getAccountNumber))
                .collect(Collectors.toList());
    }

    @Override
    public List<Account> searchAccountByCustomerName(String q) {
        String query = (q==null) ?"":q.toLowerCase();
        List<Account> result = new ArrayList<>();
        for(Customer c : customerrepository.findAll()){
            if(c.getName().toLowerCase().contains(query))
                result.addAll(accountrepository.findByCustomerId(c.getId()));
        }
        result.sort(Comparator.comparing(Account::getAccountNumber));
        return result;
    }

    private String getAccountNumber() {
        int size  = accountrepository.findAll().size()+1;
        return  String.format("AC%06d",size);

    }
}
