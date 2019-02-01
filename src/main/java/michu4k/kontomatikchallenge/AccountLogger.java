package michu4k.kontomatikchallenge;

public interface AccountLogger {
    void logIntoAccount();

    String getSessionToken();

    String getUserId();
}
