package michu4k.kontomatikchallenge.userinterface;

import michu4k.kontomatikchallenge.structures.BankAccount;
import michu4k.kontomatikchallenge.structures.UserCredentials;
import michu4k.kontomatikchallenge.exceptions.BadArgumentsException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserInterface {
    private final static String OUTPUT_MSG_HEADER = "NEST ACCOUNTS BALANCES:";

    public static UserCredentials findOutUserCredentials(String[] args) {
        checkNumberOfArguments(args.length);
        UserCredentials userCredentials = fillCredentials(args);
        checkForBlankCredentials(userCredentials);
        return userCredentials;
    }

    public static void printBankAccounts(List<BankAccount> bankAccounts) {
        System.out.println(OUTPUT_MSG_HEADER);
        for (int bankAccountIdx = 0; bankAccountIdx < bankAccounts.size(); bankAccountIdx++)
            BankAccountPrinter.printBankAccount(bankAccountIdx, bankAccounts.get(bankAccountIdx));
    }

    private static void checkNumberOfArguments(int argsCount) {
        if (argsCount != 3)
            throw new BadArgumentsException();
    }

    private static UserCredentials fillCredentials(String[] credentials) {
        UserCredentials userCredentials = new UserCredentials();
        userCredentials.login = credentials[0];
        userCredentials.password = credentials[1];
        if (!isValidAvatarId(credentials[2])) {
            throw new BadArgumentsException();
        }
        userCredentials.avatarId = Integer.parseInt(credentials[2]);
        return userCredentials;
    }

    private static void checkForBlankCredentials(UserCredentials userCredentials) {
        if (!userCredentials.isEverythingFilled())
            throw new BadArgumentsException();
    }

    private static boolean isValidAvatarId(String str) {
        Pattern intPattern = Pattern.compile("\\d{1,9}");
        Matcher intMatcher = intPattern.matcher(str);
        return intMatcher.matches();
    }
}