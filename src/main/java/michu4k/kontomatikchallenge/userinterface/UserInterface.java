package michu4k.kontomatikchallenge.userinterface;

import michu4k.kontomatikchallenge.datastructures.BankAccount;
import michu4k.kontomatikchallenge.datastructures.UserCredentials;
import michu4k.kontomatikchallenge.exceptions.BadArgumentsException;

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
            BankAccountPrinter.printBankAccountIdx(bankAccountIdx);
            BankAccountPrinter.printBankAccountName(bankAccounts.get(bankAccountIdx).accountName);
            BankAccountPrinter.printBankAccountNumber(bankAccounts.get(bankAccountIdx).accountNumber);
            BankAccountPrinter.printBankAccountBalance(
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
}
