package michu4k.kontomatikchallenge.userinterface;

public class ErrorsPrinter {
    private final static String CONNECTION_ERROR_MSG = "Connection error.";
    private final static String BAD_LOGIN_MSG = "Bad login.";
    private final static String BAD_PASSWORD_MSG = "Bad password.";
    private final static String BAD_AVATAR_ID_MSG = "Bad avatar id.";
    private final static String BAD_CREDENTIALS_MSG = "Bad credentials.";
    private final static String ARGUMENT_ERROR_MSG = "Bad arguments. Usage: NestChecker [login] [password] [avatarId]";

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

    public static void printBadAvatarIdError() {
        System.out.println(BAD_AVATAR_ID_MSG);
    }
}
