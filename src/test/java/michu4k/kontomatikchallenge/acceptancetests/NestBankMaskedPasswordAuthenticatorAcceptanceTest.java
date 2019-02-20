package michu4k.kontomatikchallenge.acceptancetests;

import com.gargoylesoftware.htmlunit.WebClient;

import michu4k.kontomatikchallenge.bankauthentication.BankAuthenticator;
import michu4k.kontomatikchallenge.bankauthentication.NestBankMaskedPasswordAuthenticator;
import michu4k.kontomatikchallenge.structures.BankSession;
import michu4k.kontomatikchallenge.exceptions.badcredentials.BadCredentialsException;
import michu4k.kontomatikchallenge.exceptions.badcredentials.BadLoginNameException;
import michu4k.kontomatikchallenge.exceptions.badcredentials.BadPasswordException;
import michu4k.kontomatikchallenge.exceptions.BadArgumentsException;
import michu4k.kontomatikchallenge.utils.factories.WebClientFactory;

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
        BankSession testOutputSession = AcceptanceTestsCommons.signIn(new String[] {validLoginName, validPassword, validAvatarId}, bankAuthenticator);
        assertFalse(testOutputSession.isSessionBlank());
    }

    @Test(expectedExceptions = BadArgumentsException.class)
    public void badFunctionArgumentsTest() throws IOException {
        AcceptanceTestsCommons.signIn(new String[] {"somelogin", "somepassword123456", "nonnumberid"}, bankAuthenticator);
    }

    @Test(expectedExceptions = BadLoginNameException.class)
    @Parameters({"tooShortLoginName", "validPassword", "validAvatarId"})
    public void loginTooShortTest(String tooShortLoginName, String validPassword, String validAvatarId) throws IOException {
        AcceptanceTestsCommons.signIn(new String[] {tooShortLoginName, validPassword, validAvatarId}, bankAuthenticator);
    }

    @Test(expectedExceptions = BadCredentialsException.class)
    @Parameters({"badLoginName", "validPassword", "validAvatarId"})
    public void badLoginTest(String badLoginName, String validPassword, String validAvatarId) throws IOException {
        AcceptanceTestsCommons.signIn(new String[] {badLoginName, validPassword, validAvatarId}, bankAuthenticator);
    }

    @Test(dependsOnMethods = { "successfulLoginTest" }, expectedExceptions = BadPasswordException.class)
    @Parameters({"validLoginName", "tooShortPassword", "validAvatarId"})
    public void passwordTooShortTest(String validLoginName, String tooShortPassword, String validAvatarId) throws IOException {
        AcceptanceTestsCommons.signIn(new String[] {validLoginName, tooShortPassword, validAvatarId}, bankAuthenticator);
    }

    @Test(dependsOnMethods = { "successfulLoginTest" }, expectedExceptions = BadCredentialsException.class)
    @Parameters({"validLoginName", "badPassword", "validAvatarId"})
    public void badPasswordTest(String validLoginName, String badPassword, String validAvatarId) throws IOException {
        AcceptanceTestsCommons.signIn(new String[] {validLoginName, badPassword, validAvatarId}, bankAuthenticator);
    }

    @Test(dependsOnMethods = { "successfulLoginTest" }, expectedExceptions = BadCredentialsException.class)
    @Parameters({"validLoginName", "validPassword", "badAvatarId"})
    public void badAvatarIdTest(String validLoginName, String validPassword, String badAvatarId) throws IOException {
        AcceptanceTestsCommons.signIn(new String[] {validLoginName, validPassword, badAvatarId}, bankAuthenticator);
    }
}