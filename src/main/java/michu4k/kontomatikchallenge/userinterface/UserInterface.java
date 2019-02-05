package michu4k.kontomatikchallenge.userinterface;

import michu4k.kontomatikchallenge.datastructures.BankAccountData;
import michu4k.kontomatikchallenge.datastructures.UserCredentials;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class UserInterface {
    private final static String ASK_LOGIN_MSG = "Type your login:";
    private final static String ASK_PASSWORD_MSG = "Type your password:";
    private final static String ASK_AVATAR_MSG = "Type your avatar id number:";
    private final static String OUTPUT_MSG_HEADER = "\nNEST ACCOUNTS BALANCES:";

    public static UserCredentials findOutUserCredentials(String[] args) {
        UserCredentials userCredentials = new UserCredentials();

        switch (args.length) {
            case 0:
                // ask for login, password, avatarId
                userCredentials.setLogin(UserInterface.askForLogin());
                userCredentials.setPassword(UserInterface.askForPassword());
                userCredentials.setAvatarId(UserInterface.askForAvatar());
                break;

            case 1:
                // ask for password and avatarId
                userCredentials.setLogin(args[0]);
                userCredentials.setPassword(UserInterface.askForPassword());
                userCredentials.setAvatarId(UserInterface.askForAvatar());
                break;

            case 2:
                // ask only for avatarId
                userCredentials.setLogin(args[0]);
                userCredentials.setPassword(args[1]);
                userCredentials.setAvatarId(UserInterface.askForAvatar());
                break;

            case 3:
                // don't ask - everything is in commandline arguments
                userCredentials.setLogin(args[0]);
                userCredentials.setPassword(args[1]);
                userCredentials.setAvatarId(Integer.parseInt(args[2]));
                break;

            default:
                ErrorsPrinter.printArgumentsError();
                System.exit(2); // terminate program with error status
                break;
        }

        // check for blank arguments/inputs
        if (!userCredentials.isEverythingFilled()) {
            ErrorsPrinter.printArgumentsError();
            System.exit(2);
        }

        return userCredentials;
    }

    public static String askForLogin() {
        System.out.println(ASK_LOGIN_MSG);
        return readUserAnswer();
    }

    public static String askForPassword() {
        System.out.println(ASK_PASSWORD_MSG);
        return readUserAnswer();
    }

    public static int askForAvatar() {
        System.out.println(ASK_AVATAR_MSG);
        int userId = -1;
        try {
            userId = Integer.parseInt(readUserAnswer());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            ErrorsPrinter.printBadAvatarIdError();
            System.exit(2);
        }
        return userId;
    }

    private static String readUserAnswer() {
        Scanner input = new Scanner(System.in);
        return input.nextLine();
    }

    public static void printAccountsBalance(List<BankAccountData> bankAccountsData) {
        System.out.println(OUTPUT_MSG_HEADER);
        for (int accountIdx = 0; accountIdx < bankAccountsData.size(); accountIdx++) {
            System.out.println(new StringBuilder("#")
                    .append(accountIdx)
                    .append("\naccount name: ")
                    .append(bankAccountsData.get(accountIdx).getAccountName())
                    .append("\naccount number: ")
                    .append(Arrays.toString(bankAccountsData.get(accountIdx).getAccountNumber()).replaceAll("\\D", ""))
                    .append("\naccount balance: ")
                    .append(bankAccountsData.get(accountIdx).getAccountBalance())
                    .append(" ")
                    .append(bankAccountsData.get(accountIdx).getAccountCurrency())
                    .toString()
            );
        }
    }
}
