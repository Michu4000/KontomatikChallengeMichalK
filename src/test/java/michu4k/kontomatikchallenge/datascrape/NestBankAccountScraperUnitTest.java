package michu4k.kontomatikchallenge.datascrape;

import com.gargoylesoftware.htmlunit.WebClient;

import michu4k.kontomatikchallenge.datastructures.BankAccount;
import michu4k.kontomatikchallenge.datastructures.BankSession;
import michu4k.kontomatikchallenge.stubs.WebClientStub;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class NestBankAccountScraperUnitTest {
    private WebClient webClientStub;
    private NestBankAccountScraper scraper;

    @BeforeMethod
    public void init() {
        webClientStub = new WebClientStub();
        scraper = new NestBankAccountScraper(webClientStub);
    }

    @Test
    public void successfulScrapeAccountsTest() throws IOException {
        BankSession validBankSession = getValidBankSession();
        List<BankAccount> expectedBankAccounts = getExpectedBankAccounts();
        List<BankAccount> testBankAccounts = scraper.scrapeBankAccounts(validBankSession);
        assertEquals(testBankAccounts, expectedBankAccounts);
    }

    private BankSession getValidBankSession() {
        BankSession bankSession = new BankSession();
        bankSession.userId = ((WebClientStub)webClientStub).getUserId();
        bankSession.sessionToken = ((WebClientStub)webClientStub).validSessionToken;
        return bankSession;
    }

    private List<BankAccount> getExpectedBankAccounts() {
        BankAccount expectedBankAccountRegular = getExpectedBankAccountRegular();
        BankAccount expectedBankAccountSavings = getExpectedBankAccountSavings();
        List<BankAccount> expectedBankAccounts = new ArrayList<>();
        expectedBankAccounts.add(expectedBankAccountRegular);
        expectedBankAccounts.add(expectedBankAccountSavings);
        return expectedBankAccounts;
    }

    private BankAccount getExpectedBankAccountRegular() {
        BankAccount expectedBankAccountRegular = getExpectedBankAccount();
        int[] accountNumberRegular = {6, 6, 5, 5, 5, 5, 4, 4, 4, 4, 3, 3, 3, 3, 2, 2, 2, 2, 1, 1, 1, 1, 0, 0, 0, 0};
        expectedBankAccountRegular.accountName = "Nest Konto";
        expectedBankAccountRegular.accountNumber = accountNumberRegular;
        expectedBankAccountRegular.accountBalance = new BigDecimal("2000.00");
        return expectedBankAccountRegular;
    }

    private BankAccount getExpectedBankAccountSavings() {
        BankAccount expectedBankAccountSavings = getExpectedBankAccount();
        int[] accountNumberSavings = {0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 6, 6 ,6, 6};
        expectedBankAccountSavings.accountName = "Nest Oszczędności";
        expectedBankAccountSavings.accountNumber = accountNumberSavings;
        expectedBankAccountSavings.accountBalance = new BigDecimal("3000.00");
        return expectedBankAccountSavings;
    }

    private BankAccount getExpectedBankAccount() {
        BankAccount expectedBankAccount = new BankAccount();
        expectedBankAccount.accountCurrency = "PLN";
        return expectedBankAccount;
    }
}