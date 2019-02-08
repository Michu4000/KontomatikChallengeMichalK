package michu4k.kontomatikchallenge.userinterface;

import michu4k.kontomatikchallenge.datastructures.BankAccount;
import michu4k.kontomatikchallenge.datastructures.UserCredentials;
import michu4k.kontomatikchallenge.exceptions.BadArgumentsException;

import java.util.Arrays;
import java.util.List;

public class UserInterface {
    private final static String OUTPUT_MSG_HEADER = "\nNEST ACCOUNTS BALANCES:";

    public static UserCredentials findOutUserCredentials(String[] args) {
        if (args.length != 3)
            throw new BadArgumentsException();

        UserCredentials userCredentials = new UserCredentials();
        userCredentials.login = args[0];
        userCredentials.password = args[1];
        userCredentials.avatarId = Integer.parseInt(args[2]);

        // check for blank arguments
        if (!userCredentials.isEverythingFilled())
            throw new BadArgumentsException();

        return userCredentials;
    }

    public static void printBankAccounts(List<BankAccount> bankAccounts) {
        System.out.println(OUTPUT_MSG_HEADER);
        for (int bankAccountIdx = 0; bankAccountIdx < bankAccounts.size(); bankAccountIdx++) {
            System.out.println("#" + bankAccountIdx);
            System.out.println("account name: " + bankAccounts.get(bankAccountIdx).accountName);
            System.out.println("account number: ");
            System.out.print(Arrays.toString(bankAccounts.get(bankAccountIdx).accountNumber)
                                                .replaceAll("\\D", ""));
            System.out.println("account balance: " + bankAccounts.get(bankAccountIdx).accountBalance + " ");
            System.out.print(bankAccounts.get(bankAccountIdx).accountCurrency);
        }
    }
}
