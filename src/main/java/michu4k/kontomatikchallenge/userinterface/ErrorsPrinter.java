package michu4k.kontomatikchallenge.userinterface;

public class ErrorsPrinter {
    private final static String ARGUMENT_ERROR_MSG = "Bad arguments. Usage: NestChecker [login] [password] [avatarId]";
    private final static String CONNECTION_ERROR_MSG = "Connection error.";
    private final static String BAD_CREDENTIALS_MSG = "Bad credentials.";
    private final static String BAD_LOGIN_MSG = "Bad login.";
    private final static String BAD_PASSWORD_MSG = "Bad password.";
    private final static String INTERNAL_ERROR_MSG = "Internal application error.";

    public static void printArgumentsError() {
        System.out.println(ARGUMENT_ERROR_MSG);
    }

    public static void printConnectionError(){
        System.out.println(CONNECTION_ERROR_MSG);
    }

    public static void printBadCredentialsError(){
        System.out.println(BAD_CREDENTIALS_MSG);
    }

    public static void printBadLoginError(){
        System.out.println(BAD_LOGIN_MSG);
    }

    public static void printBadPasswordError(){
        System.out.println(BAD_PASSWORD_MSG);
    }

    public static void printInternalApplicationError() {
        System.out.println(INTERNAL_ERROR_MSG);
    }
}
