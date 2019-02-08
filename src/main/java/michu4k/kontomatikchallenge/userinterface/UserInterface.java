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

    public static void printBankAccounts(List<BankAccount> bankAccountsData) {
        System.out.println(OUTPUT_MSG_HEADER);
        for (int bankAccountIdx = 0; bankAccountIdx < bankAccountsData.size(); bankAccountIdx++) {
            //TODO don't use StringBuilder (?)
            System.out.println(new StringBuilder("#")
                    .append(bankAccountIdx)
                    .append("\naccount name: ")
                    .append(bankAccountsData.get(bankAccountIdx).accountName)
                    .append("\naccount number: ")
                    .append(Arrays.toString(bankAccountsData.get(bankAccountIdx)
                            .accountNumber).replaceAll("\\D", ""))
                    .append("\naccount balance: ")
                    .append(bankAccountsData.get(bankAccountIdx).accountBalance)
                    .append(" ")
                    .append(bankAccountsData.get(bankAccountIdx).accountCurrency)
                    .toString()
            );
        }
    }
}
