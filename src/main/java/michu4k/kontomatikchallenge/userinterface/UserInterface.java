package michu4k.kontomatikchallenge.userinterface;

import michu4k.kontomatikchallenge.datastructures.BankAccount;
import michu4k.kontomatikchallenge.datastructures.UserCredentials;
import michu4k.kontomatikchallenge.exceptions.BadArgumentsException;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class UserInterface {
    private final static String OUTPUT_MSG_HEADER = "NEST ACCOUNTS BALANCES:";

    public static UserCredentials findOutUserCredentials(String[] args) {
        checkNumberOfArguments(args.length);

        try {
            UserCredentials userCredentials = fillCredentials(args);
            checkForBlankCredentials(userCredentials);
            return userCredentials;
        } catch (NumberFormatException numberFormatException) {
            throw new BadArgumentsException(numberFormatException);
        }
    }

    public static void printBankAccounts(List<BankAccount> bankAccounts) {
        System.out.println(OUTPUT_MSG_HEADER);
        for (int bankAccountIdx = 0; bankAccountIdx < bankAccounts.size(); bankAccountIdx++) {
            printBankAccountIdx(bankAccountIdx);
            printBankAccountName(bankAccounts.get(bankAccountIdx).accountName);
            printBankAccountNumber(bankAccounts.get(bankAccountIdx).accountNumber);
            printBankAccountBalance(
                    bankAccounts.get(bankAccountIdx).accountBalance,
                    bankAccounts.get(bankAccountIdx).accountCurrency
            );
        }
    }

    private static void checkNumberOfArguments(int argsCount) {
        if (argsCount != 3)
            throw new BadArgumentsException();
    }

    private static UserCredentials fillCredentials(String[] credentials) {
        UserCredentials userCredentials = new UserCredentials();
        userCredentials.login = credentials[0];
        userCredentials.password = credentials[1];
        userCredentials.avatarId = Integer.parseInt(credentials[2]);
        return userCredentials;
    }

    private static void checkForBlankCredentials(UserCredentials userCredentials) {
        if (!userCredentials.isEverythingFilled())
            throw new BadArgumentsException();
    }

    private static void printBankAccountIdx(int accountIdx) {
        System.out.println("#" + accountIdx);
    }

    private static void printBankAccountName(String accountName) {
        System.out.println("account name: " + accountName);
    }

    private static void printBankAccountNumber(int[] accountNumber) {
        String formattedAccountNumber = formatAccountNumber(accountNumber);
        System.out.println("account number: " + formattedAccountNumber);
    }

    private static void printBankAccountBalance(BigDecimal accountBalance, String accountCurrency) {
        String formattedBalance = formatAccountBalance(accountBalance);
        System.out.println("account balance: " + formattedBalance + " " + accountCurrency);
    }

    private static String formatAccountNumber(int[] accountNumber) {
        String unformattedAccountNumber = (Arrays.toString(accountNumber).replaceAll("\\D", ""));
        StringBuilder formattedAccountNumberBuilder = new StringBuilder();
        // group first 2 digits
        formattedAccountNumberBuilder.append(unformattedAccountNumber, 0, 2);
        // then group every 4 digits
        for(int i = 2; i <= 22; i += 4) {
            formattedAccountNumberBuilder
                    .append(" ")
                    .append(unformattedAccountNumber, i, i+4);
        }
        // iban format: XX XXXX XXXX XXXX XXXX XXXX XXXX
        return formattedAccountNumberBuilder.toString();
    }

    private static String formatAccountBalance(BigDecimal accountBalance) {
        DecimalFormat balanceFormat = new DecimalFormat("#.00");
        return balanceFormat.format(accountBalance);
    }
}
