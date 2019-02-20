package michu4k.kontomatikchallenge.acceptancetests;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;

import michu4k.kontomatikchallenge.bankauthentication.BankAuthenticator;
import michu4k.kontomatikchallenge.bankauthentication.NestBankMaskedPasswordAuthenticator;
import michu4k.kontomatikchallenge.scrapers.BankAccountScraper;
import michu4k.kontomatikchallenge.scrapers.NestBankAccountScraper;
import michu4k.kontomatikchallenge.structures.BankAccount;
import michu4k.kontomatikchallenge.structures.BankSession;
import michu4k.kontomatikchallenge.structures.UserCredentials;
import michu4k.kontomatikchallenge.userinterface.UserInterface;
import michu4k.kontomatikchallenge.utils.WebClientFactory;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.annotations.Parameters;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

public class NestBankAccountScraperAcceptanceTest {
    private BankAuthenticator bankAuthenticator;
    private BankAccountScraper bankAccountScraper;

    @BeforeClass
    void testInit() {
        WebClient webClient = WebClientFactory.getWebClient();
        bankAuthenticator = new NestBankMaskedPasswordAuthenticator(webClient);
        bankAccountScraper = new NestBankAccountScraper(webClient);
    }

    @Test
    @Parameters({"validLoginName", "validPassword", "validAvatarId"})
    public void successfulImportBankAccountsTest(String validLoginName, String validPassword, String validAvatarId) throws IOException {
        BankSession bankSession = successfulLogin(validLoginName, validPassword, validAvatarId);
        List<BankAccount> bankAccounts = bankAccountScraper.scrapeBankAccounts(bankSession);
        boolean areThereAnyMoney = bankAccounts.get(1).isInCredit();
        assertTrue(areThereAnyMoney);
    }

    @Test(expectedExceptions = FailingHttpStatusCodeException.class)
    public void blankSessionTest() throws IOException {
        bankAccountScraper.scrapeBankAccounts(new BankSession());
    }

    @Test(expectedExceptions = FailingHttpStatusCodeException.class)
    @Parameters({"validLoginName", "validPassword", "validAvatarId", "badUserId"})
    public void badUserIdTest(String validLoginName, String validPassword, String validAvatarId, int badUserId) throws IOException {
        BankSession bankSession = successfulLogin(validLoginName, validPassword, validAvatarId);
        bankSession.userId = badUserId;
        bankAccountScraper.scrapeBankAccounts(bankSession);
    }

    @Test(expectedExceptions = FailingHttpStatusCodeException.class)
    @Parameters({"validLoginName", "validPassword", "validAvatarId", "badSessionToken"})
    public void badSessionTokenTest(String validLoginName, String validPassword, String validAvatarId, String badSessionToken) throws IOException {
        BankSession bankSession = successfulLogin(validLoginName, validPassword, validAvatarId);
        bankSession.sessionToken = badSessionToken;
        bankAccountScraper.scrapeBankAccounts(bankSession);
    }

    private BankSession successfulLogin(String validLoginName, String validPassword, String validAvatarId) throws IOException {
        UserCredentials userCredentials = UserInterface.findOutUserCredentials(new String[] { validLoginName, validPassword, validAvatarId });
        return bankAuthenticator.logIntoBankAccount(userCredentials);
    }
}