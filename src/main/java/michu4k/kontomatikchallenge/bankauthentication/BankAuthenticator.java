package michu4k.kontomatikchallenge.bankauthentication;

import michu4k.kontomatikchallenge.datastructures.BankSession;
import michu4k.kontomatikchallenge.datastructures.UserCredentials;

import java.io.IOException;

public interface BankAuthenticator {
    BankSession logIntoBankAccount(UserCredentials userCredentials) throws IOException;
}
