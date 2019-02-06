package michu4k.kontomatikchallenge.bankauthentication;

import michu4k.kontomatikchallenge.datastructures.BankSession;
import michu4k.kontomatikchallenge.datastructures.UserCredentials;
import michu4k.kontomatikchallenge.exceptions.BadCredentialsException;
import michu4k.kontomatikchallenge.exceptions.BankConnectionException;

import java.net.MalformedURLException;

public interface BankAuthenticator {
    BankSession logIntoBankAccount(UserCredentials userCredentials) throws BankConnectionException, BadCredentialsException, MalformedURLException;
}
