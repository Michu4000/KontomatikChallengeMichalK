package michu4k.kontomatikchallenge.bankauthentication;

import michu4k.kontomatikchallenge.structures.BankSession;
import michu4k.kontomatikchallenge.structures.UserCredentials;

import java.io.IOException;

public interface BankAuthenticator {
    BankSession logIntoBankAccount(UserCredentials userCredentials) throws IOException;
}