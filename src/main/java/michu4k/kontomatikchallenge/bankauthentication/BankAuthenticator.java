package michu4k.kontomatikchallenge.bankauthentication;

public interface BankAuthenticator {
    void logIntoAccount();

    String getSessionToken();

    String getUserId();
}
