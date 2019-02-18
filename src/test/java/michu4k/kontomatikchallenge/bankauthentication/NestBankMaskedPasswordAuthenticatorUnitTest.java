package michu4k.kontomatikchallenge.bankauthentication;

import com.gargoylesoftware.htmlunit.WebClient;

import michu4k.kontomatikchallenge.datastructures.BankSession;
import michu4k.kontomatikchallenge.datastructures.UserCredentials;
import michu4k.kontomatikchallenge.stubs.WebClientStub;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

import java.io.IOException;

public class NestBankMaskedPasswordAuthenticatorUnitTest {
    private WebClient webClientStub;
    private NestBankMaskedPasswordAuthenticator authenticator;

    @BeforeMethod
    public void init() {
        webClientStub = new WebClientStub();
        authenticator = new NestBankMaskedPasswordAuthenticator(webClientStub);
    }

    @Test
    public void successfulLoginTest() throws IOException {
        UserCredentials userCredentials = getValidUserCredentials();
        BankSession expectedBankSession = getExpectedBankSession();

        BankSession testBankSession = authenticator.logIntoBankAccount(userCredentials);

        assertEquals(testBankSession, expectedBankSession);
    }

    private UserCredentials getValidUserCredentials() {
        UserCredentials userCredentials = new UserCredentials();
        userCredentials.login = ((WebClientStub)webClientStub).validLoginName;
        userCredentials.password = ((WebClientStub)webClientStub).validPassword;
        userCredentials.avatarId = ((WebClientStub)webClientStub).validAvatarId;
        return  userCredentials;
    }

    private BankSession getExpectedBankSession() {
        BankSession bankSession = new BankSession();
        bankSession.userId = ((WebClientStub)webClientStub).getUserId();
        bankSession.sessionToken = ((WebClientStub)webClientStub).validSessionToken;
        return bankSession;
    }
}