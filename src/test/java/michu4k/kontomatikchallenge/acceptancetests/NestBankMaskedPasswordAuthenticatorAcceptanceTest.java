package michu4k.kontomatikchallenge.acceptancetests;

import com.gargoylesoftware.htmlunit.WebClient;

import michu4k.kontomatikchallenge.bankauthentication.BankAuthenticator;
import michu4k.kontomatikchallenge.bankauthentication.NestBankMaskedPasswordAuthenticator;
import michu4k.kontomatikchallenge.structures.BankSession;
import michu4k.kontomatikchallenge.structures.UserCredentials;
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
        BankSession testOutputSession = signIn(validLoginName, validPassword, validAvatarId);
        assertFalse(testOutputSession.isSessionBlank());
    }

    @Test(expectedExceptions = BadArgumentsException.class)
    public void badFunctionArgumentsTest() throws IOException {
        signIn("somelogin", "somepassword123456", "nonnumberid");
    }

    @Test(expectedExceptions = BadLoginNameException.class)
    @Parameters({"tooShortLoginName", "validPassword", "validAvatarId"})
    public void loginTooShortTest(String tooShortLoginName, String validPassword, String validAvatarId) throws IOException {
        signIn(tooShortLoginName, validPassword, validAvatarId);
    }

    @Test(expectedExceptions = BadCredentialsException.class)
    @Parameters({"badLoginName", "validPassword", "validAvatarId"})
    public void badLoginTest(String badLoginName, String validPassword, String validAvatarId) throws IOException {
        signIn(badLoginName, validPassword, validAvatarId);
    }

    @Test(dependsOnMethods = { "successfulLoginTest" }, expectedExceptions = BadPasswordException.class)
    @Parameters({"validLoginName", "tooShortPassword", "validAvatarId"})
    public void passwordTooShortTest(String validLoginName, String tooShortPassword, String validAvatarId) throws IOException {
        signIn(validLoginName, tooShortPassword, validAvatarId);
    }

    @Test(dependsOnMethods = { "successfulLoginTest" }, expectedExceptions = BadCredentialsException.class)
    @Parameters({"validLoginName", "badPassword", "validAvatarId"})
    public void badPasswordTest(String validLoginName, String badPassword, String validAvatarId) throws IOException {
        signIn(validLoginName, badPassword, validAvatarId);
    }

    @Test(dependsOnMethods = { "successfulLoginTest" }, expectedExceptions = BadCredentialsException.class)
    @Parameters({"validLoginName", "validPassword", "badAvatarId"})
    public void badAvatarIdTest(String validLoginName, String validPassword, String badAvatarId) throws IOException {
        signIn(validLoginName, validPassword, badAvatarId);
    }

    private BankSession signIn(String loginName, String password, String avatarId) throws IOException {
        UserCredentials userCredentials = UserInterface.findOutUserCredentials(new String[] {loginName, password, avatarId});
        return bankAuthenticator.logIntoBankAccount(userCredentials);
    }
}