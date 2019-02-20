package michu4k.kontomatikchallenge.acceptancetests;

import michu4k.kontomatikchallenge.bankauthentication.BankAuthenticator;
import michu4k.kontomatikchallenge.structures.BankSession;
import michu4k.kontomatikchallenge.structures.UserCredentials;
import michu4k.kontomatikchallenge.userinterface.UserInterface;

import java.io.IOException;

class AcceptanceTestsCommons {
    static BankSession signIn(String[] credentialsArgs, BankAuthenticator bankAuthenticator) throws IOException {
        UserCredentials userCredentials = UserInterface.findOutUserCredentials(credentialsArgs);
        return bankAuthenticator.logIntoBankAccount(userCredentials);
    }
}
