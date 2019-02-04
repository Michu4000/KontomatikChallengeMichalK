package michu4k.kontomatikchallenge.userinterface;

import java.util.List;
import java.util.Scanner;

public class UserInterface {
    private final static String ASK_LOGIN_MSG = "Type your login:";
    private final static String ASK_PASSWORD_MSG = "Type your password:";
    private final static String ASK_AVATAR_MSG = "Type your avatar id number:";
    private final static String CONNECTION_ERROR_MSG = "Connection error.";
    private final static String BAD_LOGIN_MSG = "Bad login.";
    private final static String BAD_PASSWORD_MSG = "Bad password.";
    private final static String BAD_CREDENTIALS_MSG = "Bad credentials.";
    private final static String ARGUMENT_ERROR_MSG = "Bad arguments. Usage: Main [login] [password] [avatarId]";
    private final static String OUTPUT_MSG_HEADER = "\nNEST ACCOUNTS BALANCES:";

    public static String askForLogin() {
        System.out.println(ASK_LOGIN_MSG);
        return readUserAnswer();
    }

    public static String askForPassword() {
        System.out.println(ASK_PASSWORD_MSG);
        return readUserAnswer();
    }

    public static String askForAvatar() {
        System.out.println(ASK_AVATAR_MSG);
        return readUserAnswer();
    }

    private static String readUserAnswer() {
        final Scanner input = new Scanner(System.in);
        return input.nextLine();
    }

    public static void printArgumentsError() {
        System.out.println(ARGUMENT_ERROR_MSG);
    }

    public static void printConnectionError(){
        System.out.println(CONNECTION_ERROR_MSG);
    }

    public static void printBadLoginError(){
        System.out.println(BAD_LOGIN_MSG);
    }

    public static void printBadPasswordError(){
        System.out.println(BAD_PASSWORD_MSG);
    }

    public static void printCredentialsError(){
        System.out.println(BAD_CREDENTIALS_MSG);
    }

    public static void printAccountsBalance(List<String> accountsNames,
                                     List<String> accountsNumbers,
                                     List<String> accountsBalances,
                                     List<String> accountsCurrencies) {
        System.out.println(OUTPUT_MSG_HEADER);
        for (int accountIdx = 0; accountIdx < accountsNames.size(); accountIdx++) {
            System.out.println(new StringBuilder("#")
                    .append(accountIdx)
                    .append("\naccount name: ")
                    .append(accountsNames.get(accountIdx))
                    .append("\naccount number: ")
                    .append(accountsNumbers.get(accountIdx))
                    .append("\naccount balance: ")
                    .append(accountsBalances.get(accountIdx))
                    .append(" ")
                    .append(accountsCurrencies.get(accountIdx))
                    .toString()
            );
        }
    }
}
