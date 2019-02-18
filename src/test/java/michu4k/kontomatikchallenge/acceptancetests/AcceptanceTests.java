package michu4k.kontomatikchallenge.acceptancetests;

import com.gargoylesoftware.htmlunit.WebClient;

import michu4k.kontomatikchallenge.bankauthentication.BankAuthenticator;
import michu4k.kontomatikchallenge.bankauthentication.NestBankMaskedPasswordAuthenticator;
import michu4k.kontomatikchallenge.datascrape.BankAccountScraper;
import michu4k.kontomatikchallenge.datascrape.NestBankAccountScraper;
import michu4k.kontomatikchallenge.datastructures.BankAccount;
import michu4k.kontomatikchallenge.datastructures.BankSession;
import michu4k.kontomatikchallenge.datastructures.UserCredentials;
import michu4k.kontomatikchallenge.exceptions.BadArgumentsException;
import michu4k.kontomatikchallenge.exceptions.BadCredentialsException;
import michu4k.kontomatikchallenge.exceptions.BadLoginNameException;
import michu4k.kontomatikchallenge.exceptions.BadPasswordException;
import michu4k.kontomatikchallenge.userinterface.UserInterface;
import michu4k.kontomatikchallenge.utils.WebClientFactory;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.Parameters;

import static org.testng.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AcceptanceTests {
    private BankAuthenticator bankAuthenticator;
    private  BankAccountScraper bankAccountScraper;
    private ByteArrayOutputStream byteArrayOutputStream;
    private PrintStream standardPrintStream;
    private String expectedOutputBeginning;
    private int expectedOutputBeginningLength;
    private String testOutputBeginning;

    @BeforeClass void testInit() {
        WebClient webClient = WebClientFactory.getWebClient();
        bankAuthenticator = new NestBankMaskedPasswordAuthenticator(webClient);
        bankAccountScraper = new NestBankAccountScraper(webClient);
    }

    @BeforeMethod
    public void changePrintStreamForCapture() {
        byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream capturedPrintStream = new PrintStream(byteArrayOutputStream);
        standardPrintStream = System.out;
        System.setOut(capturedPrintStream);
    }

    @AfterMethod
    public void changePrintStreamBackToNormal() {
        System.out.flush();
        System.setOut(standardPrintStream);
    }

    @Test
    @Parameters({"validLoginName", "validPassword", "validAvatarId"})
    public void successfulLoginAndImportBankAccountTest(String validLoginName, String validPassword, String validAvatarId)
            throws  IOException {
        setExpectedOutputBeginning("NEST ACCOUNTS BALANCES:\n#1");

        startTest(new String[] { validLoginName, validPassword, validAvatarId });

        doAssertion();
    }

    @Test(expectedExceptions = BadArgumentsException.class)
    public void badFunctionArgumentsTest() throws  IOException {
        startTest(new String[]{"1", "2", "3", "4"});
    }

    @Test(expectedExceptions = BadLoginNameException.class)
    @Parameters({"tooShortLoginName", "validPassword", "validAvatarId"})
    public void loginTooShortTest(String tooShortLoginName, String validPassword, String validAvatarId)
            throws  IOException {
        startTest(new String[] { tooShortLoginName, validPassword, validAvatarId });
    }


    @Test(expectedExceptions = BadCredentialsException.class)
    @Parameters({"badLoginName", "validPassword", "validAvatarId"})
    public void badLoginTest(String badLoginName, String validPassword, String validAvatarId) throws  IOException {
        startTest(new String[] { badLoginName, validPassword, validAvatarId });
    }

    @Test
    (dependsOnMethods = { "successfulLoginAndImportBankAccountTest" }, expectedExceptions = BadPasswordException.class)
    @Parameters({"validLoginName", "tooShortPassword", "validAvatarId"})
    public void passwordTooShortTest(String validLoginName, String tooShortPassword, String validAvatarId)
            throws  IOException {
        startTest(new String[] { validLoginName, tooShortPassword, validAvatarId });
    }

    @Test
    (dependsOnMethods = { "successfulLoginAndImportBankAccountTest" }, expectedExceptions = BadCredentialsException.class)
    @Parameters({"validLoginName", "badPassword", "validAvatarId"})
    public void badPasswordTest(String validLoginName, String badPassword, String validAvatarId) throws  IOException {
        startTest(new String[] { validLoginName, badPassword, validAvatarId });
    }

    @Test
    (dependsOnMethods = { "successfulLoginAndImportBankAccountTest" }, expectedExceptions = BadCredentialsException.class)
    @Parameters({"validLoginName", "validPassword", "badAvatarId"})
    public void badAvatarIdTest(String validLoginName, String validPassword, String badAvatarId) throws  IOException {
        startTest(new String[] { validLoginName, validPassword, badAvatarId });
    }

    private void setExpectedOutputBeginning(String str) {
        expectedOutputBeginning = str;
        expectedOutputBeginningLength = str.length();
    }

    private void startTest(String[] args) throws IOException {
        UserCredentials userCredentials = UserInterface.findOutUserCredentials(args);
        BankSession bankSession = bankAuthenticator.logIntoBankAccount(userCredentials);
        List<BankAccount> bankAccounts = bankAccountScraper.scrapeBankAccounts(bankSession);
        UserInterface.printBankAccounts(bankAccounts);
    }

    private void doAssertion() {
        setTestOutputBeginning();
        assertEquals(testOutputBeginning, expectedOutputBeginning);
    }

    private void setTestOutputBeginning() {
        testOutputBeginning =
                new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8)
                        .substring(0, expectedOutputBeginningLength);
    }
}
