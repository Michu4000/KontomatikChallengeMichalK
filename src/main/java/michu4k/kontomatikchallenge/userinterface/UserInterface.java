package michu4k.kontomatikchallenge.userinterface;

import michu4k.kontomatikchallenge.datastructures.BankAccount;
import michu4k.kontomatikchallenge.datastructures.UserCredentials;
import michu4k.kontomatikchallenge.exceptions.BadArgumentsException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class UserInterface {
    private final static String OUTPUT_MSG_HEADER = "NEST ACCOUNTS BALANCES:";

    public static UserCredentials findOutUserCredentials(String[] args) {
        checkNumberOfArguments(args.length);

        UserCredentials userCredentials;
        try {
            userCredentials = fillCredentials(args);
        } catch (NumberFormatException numberFormatException) {
            throw new BadArgumentsException(numberFormatException);
        }

        checkForBlankCredentials(userCredentials);
        return userCredentials;
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
        System.out.println(Arrays.toString(accountNumber)
                .replaceAll("\\D", ""));
    }

    private static void printBankAccountBalance(BigDecimal accountBalance, String accountCurrency) {
        System.out.println("account balance: " + accountBalance + " " + accountCurrency);
    }
}
