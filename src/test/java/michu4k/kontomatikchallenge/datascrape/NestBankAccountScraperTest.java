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

public class NestBankAccountScraperTest {
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
        int[] accountNumberRegular = {6, 6, 5, 5, 5, 5, 4, 4, 4, 4, 3, 3, 3, 3, 2, 2, 2, 2, 1, 1, 1, 1, 0, 0, 0, 0};
        BankAccount bankAccountRegular = new BankAccount();
        bankAccountRegular.accountName = "Nest Konto";
        bankAccountRegular.accountNumber = accountNumberRegular;
        bankAccountRegular.accountBalance = new BigDecimal("2000.00");
        bankAccountRegular.accountCurrency = "PLN";

        int[] accountNumberSavings = {0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 6, 6 ,6, 6};
        BankAccount bankAccountSavings = new BankAccount();
        bankAccountSavings.accountName = "Nest Oszczędności";
        bankAccountSavings.accountNumber = accountNumberSavings;
        bankAccountSavings.accountBalance = new BigDecimal("3000.00");
        bankAccountSavings.accountCurrency = "PLN";

        List<BankAccount> bankAccounts = new ArrayList<>();
        bankAccounts.add(bankAccountRegular);
        bankAccounts.add(bankAccountSavings);
        return bankAccounts;
    }
}
