package michu4k.kontomatikchallenge;

public interface BankAuthenticator {
    void logIntoAccount();

    String getSessionToken();

    String getUserId();
}
