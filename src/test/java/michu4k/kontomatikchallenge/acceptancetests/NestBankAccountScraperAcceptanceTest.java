package michu4k.kontomatikchallenge.acceptancetests;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;

import michu4k.kontomatikchallenge.bankauthentication.BankAuthenticator;
import michu4k.kontomatikchallenge.bankauthentication.NestBankMaskedPasswordAuthenticator;
import michu4k.kontomatikchallenge.scrapers.BankAccountScraper;
import michu4k.kontomatikchallenge.scrapers.NestBankAccountScraper;
import michu4k.kontomatikchallenge.structures.BankAccount;
import michu4k.kontomatikchallenge.structures.BankSession;
import michu4k.kontomatikchallenge.utils.factories.WebClientFactory;

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
        BankSession bankSession = AcceptanceTestsCommons.signIn(new String[] {validLoginName, validPassword, validAvatarId}, bankAuthenticator);
        List<BankAccount> bankAccounts = bankAccountScraper.scrapeBankAccounts(bankSession);
        assertTrue(areThereAnyMoney(bankAccounts));
    }

    @Test(expectedExceptions = FailingHttpStatusCodeException.class)
    public void blankBankSessionTest() throws IOException {
        bankAccountScraper.scrapeBankAccounts(new BankSession());
    }

    @Test(expectedExceptions = FailingHttpStatusCodeException.class)
    @Parameters({"badSessionToken", "badUserId"})
    public void incorrectBankSessionTest(String badSessionToken, int badUserId) throws IOException {
        BankSession bankSession = new BankSession();
        bankSession.sessionToken = badSessionToken;
        bankSession.userId = badUserId;
        bankAccountScraper.scrapeBankAccounts(bankSession);
    }

    private boolean areThereAnyMoney(List<BankAccount> bankAccounts) {
        if(bankAccounts.size() > 0)
            return bankAccounts.get(1).isInCredit();
        else
            return false;
    }
}