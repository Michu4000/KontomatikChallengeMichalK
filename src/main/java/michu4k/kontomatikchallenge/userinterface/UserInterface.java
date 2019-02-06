package michu4k.kontomatikchallenge.userinterface;

import michu4k.kontomatikchallenge.datastructures.BankAccountData;
import michu4k.kontomatikchallenge.datastructures.UserCredentials;
import michu4k.kontomatikchallenge.exceptions.BadArgumentsException;

import java.util.Arrays;
import java.util.List;

public class UserInterface {
    private final static String OUTPUT_MSG_HEADER = "\nNEST ACCOUNTS BALANCES:";

    public static UserCredentials findOutUserCredentials(String[] args) throws BadArgumentsException {
        if(args.length != 3) {
            throw new BadArgumentsException();
        }

        UserCredentials userCredentials = new UserCredentials();

        userCredentials.setLogin(args[0]);
        userCredentials.setPassword(args[1]);
        try {
            userCredentials.setAvatarId(Integer.parseInt(args[2]));
        } catch (NumberFormatException e) {
            BadArgumentsException badArgumentsException = new BadArgumentsException();
            badArgumentsException.setStackTrace(e.getStackTrace());
            throw badArgumentsException;
        }

        // check for blank arguments
        if (!userCredentials.isEverythingFilled()) {
            throw new BadArgumentsException();
        }

        return userCredentials;
    }

    public static void printBankAccountsBalance(List<BankAccountData> bankAccountsData) {
        System.out.println(OUTPUT_MSG_HEADER);
        for (int bankAccountIdx = 0; bankAccountIdx < bankAccountsData.size(); bankAccountIdx++) {
            System.out.println(new StringBuilder("#")
                    .append(bankAccountIdx)
                    .append("\naccount name: ")
                    .append(bankAccountsData.get(bankAccountIdx).getAccountName())
                    .append("\naccount number: ")
                    .append(Arrays.toString(bankAccountsData.get(bankAccountIdx)
                            .getAccountNumber()).replaceAll("\\D", ""))
                    .append("\naccount balance: ")
                    .append(bankAccountsData.get(bankAccountIdx).getAccountBalance())
                    .append(" ")
                    .append(bankAccountsData.get(bankAccountIdx).getAccountCurrency())
                    .toString()
            );
        }
    }
}
