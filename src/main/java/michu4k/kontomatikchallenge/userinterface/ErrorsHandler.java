package michu4k.kontomatikchallenge.userinterface;

public class ErrorsHandler {
    private final static String ARGUMENT_ERROR_MSG = "Bad arguments. Usage: NestChecker <login> <password> <avatarId>";
    private final static String CONNECTION_ERROR_MSG = "Connection error.";
    private final static String BAD_CREDENTIALS_MSG = "Bad credentials.";
    private final static String INTERNAL_ERROR_MSG = "Internal application error.";

    public static void handleException(Exception exception, boolean debugMode) {
        if (debugMode)
            exception.printStackTrace();
        printError(exception);
    }

    private static void printError(Exception exception) {
        switch(exception.getClass().getSimpleName()) {
            case "BadArgumentsException":
                printArgumentsError();
                break;
            case "IOException":
            case "JsonException":
            case "JsonParsingException":
            case "IllegalStateException":
            case "NullPointerException":
            case "ClassCastException":
                printConnectionError();
                break;
            case "BadCredentialsException":
            case "BadLoginNameException":
            case "BadPasswordException":
                printBadCredentialsError();
                break;
            case "NumberFormatException":
            case "MalformedURLException":
            default:
                printInternalApplicationError();
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