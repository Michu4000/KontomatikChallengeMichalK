package michu4k.kontomatikchallenge.utils.bankaccountstools;

import michu4k.kontomatikchallenge.structures.BankAccount;

import java.math.BigDecimal;

public class BankAccountPrinter {
    public static void printBankAccount(int accountIdx, BankAccount bankAccount) {
        printBankAccountIdx(accountIdx);
        printBankAccountName(bankAccount.accountName);
        printBankAccountNumber(bankAccount.accountNumber);
        printBankAccountBalance(bankAccount.accountBalance, bankAccount.accountCurrency);
    }

    private static void printBankAccountIdx(int accountIdx) {
        System.out.println("#" + (accountIdx + 1));
    }

    private static void printBankAccountName(String accountName) {
        System.out.println("account name: " + accountName);
    }

    private static void printBankAccountNumber(int[] accountNumber) {
        String formattedAccountNumber = BankAccountFormatter.formatAccountNumber(accountNumber);
        System.out.println("account number: " + formattedAccountNumber);
    }

    private static void printBankAccountBalance(BigDecimal accountBalance, String accountCurrency) {
        String formattedBalance = BankAccountFormatter.formatAccountBalance(accountBalance);
        System.out.println("account balance: " + formattedBalance + " " + accountCurrency);
    }
}