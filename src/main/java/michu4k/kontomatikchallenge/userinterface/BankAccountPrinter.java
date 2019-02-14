package michu4k.kontomatikchallenge.userinterface;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;

class BankAccountPrinter {
    static void printBankAccountIdx(int accountIdx) {
        System.out.println("#" + accountIdx);
    }

    static void printBankAccountName(String accountName) {
        System.out.println("account name: " + accountName);
    }

    static void printBankAccountNumber(int[] accountNumber) {
        String formattedAccountNumber = formatAccountNumber(accountNumber);
        System.out.println("account number: " + formattedAccountNumber);
    }

    static void printBankAccountBalance(BigDecimal accountBalance, String accountCurrency) {
        String formattedBalance = formatAccountBalance(accountBalance);
        System.out.println("account balance: " + formattedBalance + " " + accountCurrency);
    }

    private static String formatAccountNumber(int[] accountNumber) {
        String unformattedAccountNumber = Arrays.toString(accountNumber).replaceAll("\\D", "");
        StringBuilder formattedAccountNumberBuilder = new StringBuilder();
        // group first 2 digits
        formattedAccountNumberBuilder.append(unformattedAccountNumber, 0, 2);
        // then group every 4 digits
        for (int i = 2; i <= 22; i += 4) {
            formattedAccountNumberBuilder
                    .append(" ")
                    .append(unformattedAccountNumber, i, i + 4);
        }
        // iban format: XX XXXX XXXX XXXX XXXX XXXX XXXX
        return formattedAccountNumberBuilder.toString();
    }

    private static String formatAccountBalance(BigDecimal accountBalance) {
        DecimalFormat balanceFormat = new DecimalFormat("#.00");
        return balanceFormat.format(accountBalance);
    }
}