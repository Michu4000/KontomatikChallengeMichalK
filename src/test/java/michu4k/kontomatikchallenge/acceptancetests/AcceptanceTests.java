package michu4k.kontomatikchallenge.acceptancetests;

import michu4k.kontomatikchallenge.Main;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class AcceptanceTests {
    private ByteArrayOutputStream byteArrayOutputStream;
    private PrintStream standardPrintStream;
    private String expectedOutputBeginning;
    private int expectedOutputBeginningLength;
    private String testOutputBeginning;

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
    public void successfulLoginAndImportBankAccountTest(String validLoginName, String validPassword, String validAvatarId) {
        setExpectedOutputBeginning("NEST ACCOUNTS BALANCES:\n#1");

        startTest(new String[] { validLoginName, validPassword, validAvatarId });

        doAssertion();
    }

    @Test
    public void badFunctionArgumentsTest() {
        setExpectedOutputBeginning("Bad arguments. Usage: NestChecker <login> <password> <avatarId>");

        startTest(new String[]{"1", "2", "3", "4"});

        doAssertion();
    }

    @Test
    @Parameters({"tooShortLoginName", "validPassword", "validAvatarId"})
    public void loginTooShortTest(String tooShortLoginName, String validPassword, String validAvatarId) {
        setExpectedOutputBeginning("Bad credentials.");

        startTest(new String[] { tooShortLoginName, validPassword, validAvatarId });

        doAssertion();
    }


    @Test
    @Parameters({"badLoginName", "validPassword", "validAvatarId"})
    public void badLoginTest(String badLoginName, String validPassword, String validAvatarId) {
        setExpectedOutputBeginning("Bad credentials.");

        startTest(new String[] { badLoginName, validPassword, validAvatarId });

        doAssertion();
    }

    @Test(dependsOnMethods = { "successfulLoginAndImportBankAccountTest" })
    @Parameters({"validLoginName", "tooShortPassword", "validAvatarId"})
    public void passwordTooShortTest(String validLoginName, String tooShortPassword, String validAvatarId) {
        setExpectedOutputBeginning("Bad credentials.");

        startTest(new String[] { validLoginName, tooShortPassword, validAvatarId });

        doAssertion();
    }

    @Test(dependsOnMethods = { "successfulLoginAndImportBankAccountTest" })
    @Parameters({"validLoginName", "badPassword", "validAvatarId"})
    public void badPasswordTest(String validLoginName, String badPassword, String validAvatarId) {
        setExpectedOutputBeginning("Bad credentials.");

        startTest(new String[] { validLoginName, badPassword, validAvatarId });

        doAssertion();
    }

    @Test(dependsOnMethods = { "successfulLoginAndImportBankAccountTest" })
    @Parameters({"validLoginName", "validPassword", "badAvatarId"})
    public void badAvatarIdTest(String validLoginName, String validPassword, String badAvatarId) {
        setExpectedOutputBeginning("Bad credentials.");

        startTest(new String[] { validLoginName, validPassword, badAvatarId });

        doAssertion();
    }

    private void setExpectedOutputBeginning(String str) {
        expectedOutputBeginning = str;
        expectedOutputBeginningLength = str.length();
    }

    private void startTest(String[] args) {
        Main.main(args);
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
