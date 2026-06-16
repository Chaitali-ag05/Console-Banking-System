package app;

import Service.bankService;
import Service.impl.bankServiceimpl;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        bankService bankservice = new bankServiceimpl();
        boolean running = true;
        System.out.println("Welcome to Console Bank");

        while(running) {
            System.out.println("""
                    1) Open Account\s
                    2) Deposit
                    3) Withdraw
                    4) Transfer
                    5) Account Statement\s
                    6) List Accounts
                    7) Search Accounts by Customer Name
                    0) Exit
                   \s""");
            System.out.println("CHOOSE : ");
            String choice = sc.nextLine().trim();
            System.out.println("CHOICE : " + choice);

            switch(choice){
                case "1" -> openAccount(sc,bankservice);
                case "2" -> deposit(sc,bankservice);
                case "3" -> withdraw(sc,bankservice);
                case "4" -> transfer(sc,bankservice);
                case "5" -> statement(sc,bankservice);
                case "6" -> listAccounts(sc,bankservice);
                case "7" -> searchAccounts(sc,bankservice);
                case "0" -> running = false;
            }
        }
    }

    private static void openAccount(Scanner sc,bankService bankservice) {
        System.out.println("Customer Name : ");
        String name = sc.nextLine().trim();
        System.out.println("Customer Email : ");
        String email = sc.nextLine().trim();
        System.out.println("Account Type(Savings/Current ): ");
        String type = sc.nextLine().trim();
        System.out.println("Intial deposite (optional) : ");
        String amountstr = sc.nextLine().trim();
        if(amountstr.isBlank()) amountstr = "0";
        double intial = Double.parseDouble(amountstr);
        String accountNumber= bankservice.openAccount(name, email, type);
        if(intial>0)
            bankservice.deposit(accountNumber,intial,"Intial Deposit");
        System.out.println("Account Opened "+accountNumber);
    }


    private static void deposit(Scanner sc,bankService bankservice) {
        System.out.println("Account Number :");
         String AccountNumber =sc.nextLine().trim();
        System.out.println("Amount ; ");
        Double amount = Double.valueOf(sc.nextLine().trim());
        bankservice.deposit(AccountNumber,amount,"Deposite");
        System.out.println("Deposited");
    }

    private static void withdraw(Scanner sc,bankService bankservice) {
        System.out.println("Account Number :");
        String AccountNumber =sc.nextLine().trim();
        System.out.println("Amount ; ");
        Double amount = Double.valueOf(sc.nextLine().trim());
        bankservice.withdraw(AccountNumber,amount,"Withdraw");
        System.out.println("withdrawn");

    }


    private static void transfer(Scanner sc, bankService bankservice) {
        System.out.println("From Account :");
        String from = sc.nextLine().trim();
        System.out.println("To Account :");
        String to = sc.nextLine().trim();
        System.out.println("Amount : ");
        Double amount = Double.valueOf((sc.nextLine().trim()));
        bankservice.transfer(from , to , amount,"Transfer");

    }

    private static void statement(Scanner sc,bankService bankservice) {
        System.out.println("Account :");
        String accountNumber = sc.nextLine().trim();
        bankservice.getStatement(accountNumber).forEach(t -> System.out.println(t.getTimestamp()+" | "+t.getType()+" | "+t.getAmount()+"| "+t.getNote()));

    }

    private static void listAccounts(Scanner sc,bankService bankservice) {
        bankservice.listAccount().forEach(a -> {
            System.out.println(a.getAccountNumber()+" | "+a.getAccountType()+" | "+a.getBalance());
        });
    }

    private static void searchAccounts(Scanner sc,bankService bankservice) {
        System.out.println("Customer name contains :");
        String q = sc.nextLine().trim();
        bankservice.searchAccountByCustomerName(q).forEach(a->
                System.out.println(a.getAccountNumber()+" | "+a.getBalance()+" | "+a.getAccountType())
        );

    }



}
