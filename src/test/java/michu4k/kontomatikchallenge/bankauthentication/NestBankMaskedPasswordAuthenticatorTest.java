package michu4k.kontomatikchallenge.bankauthentication;

import com.gargoylesoftware.htmlunit.WebClient;

import michu4k.kontomatikchallenge.datastructures.BankSession;
import michu4k.kontomatikchallenge.datastructures.UserCredentials;
import michu4k.kontomatikchallenge.stubs.WebClientStub;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

import java.io.IOException;

public class NestBankMaskedPasswordAuthenticatorTest {
    @BeforeMethod
    public void init() {}

    @Test
    public void logIntoBankAccountTest() throws IOException {
        WebClient webClientStub = new WebClientStub();

        NestBankMaskedPasswordAuthenticator authenticator = new NestBankMaskedPasswordAuthenticator(webClientStub);

        UserCredentials userCredentials = new UserCredentials();
        userCredentials.login = "testtest";
        userCredentials.password = "testtest123";
        userCredentials.avatarId = 23;

        BankSession bankSession = authenticator.logIntoBankAccount(userCredentials);

        BankSession expectedBankSession = new BankSession();
        expectedBankSession.userId = 10250313;
        expectedBankSession.sessionToken = "GSAaq9o7oEEPKhzlPM4WMOn8YrKq+VMmTzjy/RkLQdFmY0IqEOMkmFA+Nb2NiqMY12riHsfiG" +
                "BZjEv3SsYSY8CtLfyJhnf/gWg2HK4AAm71zCvDAS4POmw5nQc+sC/FySYAfVqJ+YlFt5dv0XPjMUV4bSEcUwpdjhNpryKo/peIaA" +
                "gybSq4xUzD/7u2i2g/OaavCDly/2ZYE0egj6PIV/h8x1HyrZAyknjrUqE6pCf1bdOAdu5goiTnNwuicYy3J";

        assertEquals(bankSession, expectedBankSession);
    }
}
