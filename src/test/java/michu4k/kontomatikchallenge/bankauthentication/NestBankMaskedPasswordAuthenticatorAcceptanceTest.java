package michu4k.kontomatikchallenge.bankauthentication;

import com.gargoylesoftware.htmlunit.WebClient;

import michu4k.kontomatikchallenge.datastructures.BankSession;
import michu4k.kontomatikchallenge.datastructures.UserCredentials;
import michu4k.kontomatikchallenge.exceptions.BadArgumentsException;
import michu4k.kontomatikchallenge.exceptions.BadCredentialsException;
import michu4k.kontomatikchallenge.exceptions.BadLoginNameException;
import michu4k.kontomatikchallenge.exceptions.BadPasswordException;
import michu4k.kontomatikchallenge.userinterface.UserInterface;
import michu4k.kontomatikchallenge.utils.WebClientFactory;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.annotations.Parameters;

import static org.testng.Assert.assertFalse;

import java.io.IOException;

public class NestBankMaskedPasswordAuthenticatorAcceptanceTest {
    private BankAuthenticator bankAuthenticator;

    @BeforeClass
    void testInit() {
        WebClient webClient = WebClientFactory.getWebClient();
        bankAuthenticator = new NestBankMaskedPasswordAuthenticator(webClient);
    }

    @Test
    @Parameters({"validLoginName", "validPassword", "validAvatarId"})
    public void successfulLoginTest(String validLoginName, String validPassword, String validAvatarId) throws IOException {
        BankSession testOutputSession = startTest(new String[] { validLoginName, validPassword, validAvatarId });
        assertFalse(testOutputSession.isSessionBlank());
    }

    @Test(expectedExceptions = BadArgumentsException.class)
    public void badFunctionArgumentsTest() throws IOException {
        startTest(new String[]{"1", "2", "3", "4"});
    }

    @Test(expectedExceptions = BadLoginNameException.class)
    @Parameters({"tooShortLoginName", "validPassword", "validAvatarId"})
    public void loginTooShortTest(String tooShortLoginName, String validPassword, String validAvatarId) throws IOException {
        startTest(new String[] { tooShortLoginName, validPassword, validAvatarId });
    }

    @Test(expectedExceptions = BadCredentialsException.class)
    @Parameters({"badLoginName", "validPassword", "validAvatarId"})
    public void badLoginTest(String badLoginName, String validPassword, String validAvatarId) throws IOException {
        startTest(new String[] { badLoginName, validPassword, validAvatarId });
    }

    @Test(dependsOnMethods = { "successfulLoginTest" }, expectedExceptions = BadPasswordException.class)
    @Parameters({"validLoginName", "tooShortPassword", "validAvatarId"})
    public void passwordTooShortTest(String validLoginName, String tooShortPassword, String validAvatarId) throws IOException {
        startTest(new String[] { validLoginName, tooShortPassword, validAvatarId });
    }

    @Test(dependsOnMethods = { "successfulLoginTest" }, expectedExceptions = BadCredentialsException.class)
    @Parameters({"validLoginName", "badPassword", "validAvatarId"})
    public void badPasswordTest(String validLoginName, String badPassword, String validAvatarId) throws IOException {
        startTest(new String[] { validLoginName, badPassword, validAvatarId });
    }

    @Test(dependsOnMethods = { "successfulLoginTest" }, expectedExceptions = BadCredentialsException.class)
    @Parameters({"validLoginName", "validPassword", "badAvatarId"})
    public void badAvatarIdTest(String validLoginName, String validPassword, String badAvatarId) throws IOException {
        startTest(new String[] { validLoginName, validPassword, badAvatarId });
    }

    private BankSession startTest(String[] args) throws IOException {
        UserCredentials userCredentials = UserInterface.findOutUserCredentials(args);
        return bankAuthenticator.logIntoBankAccount(userCredentials);
    }
}