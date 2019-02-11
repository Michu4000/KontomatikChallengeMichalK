package michu4k.kontomatikchallenge.userinterface;

public class ErrorsHandler {

    private final static String ARGUMENT_ERROR_MSG = "Bad arguments. Usage: NestChecker <login> <password> <avatarId>";
    private final static String CONNECTION_ERROR_MSG = "Connection error.";
    private final static String BAD_CREDENTIALS_MSG = "Bad credentials.";
    private final static String INTERNAL_ERROR_MSG = "Internal application error.";

    public static void handleException(Exception exception, boolean debugMode) {
        //if (debugMode) //TODO uncomment
            exception.printStackTrace();
        printErrorAndExit(exception);
    }

    private static void printErrorAndExit(Exception exception) {
        switch(exception.getClass().getSimpleName()) {
            case "BadArgumentsException":
                printArgumentsError();
                System.exit(2);
                break;
            case "IOException":
                printConnectionError();
                System.exit(1);
                break;
            case "BadCredentialsException":
            case "BadLoginNameException":
            case "BadPasswordException":
                printBadCredentialsError();
                System.exit(2);
                break;
            default:
                printInternalApplicationError();
                System.exit(3);
                break;
        }
    }

    private static void printArgumentsError() {
        System.out.println(ARGUMENT_ERROR_MSG);
    }

    private static void printConnectionError(){
        System.out.println(CONNECTION_ERROR_MSG);
    }

    private static void printBadCredentialsError(){
        System.out.println(BAD_CREDENTIALS_MSG);
    }

    private static void printInternalApplicationError() {
        System.out.println(INTERNAL_ERROR_MSG);
    }
}
