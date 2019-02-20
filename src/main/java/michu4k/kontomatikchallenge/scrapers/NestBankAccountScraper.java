package michu4k.kontomatikchallenge.scrapers;

import michu4k.kontomatikchallenge.structures.BankAccount;
import michu4k.kontomatikchallenge.structures.BankSession;
import michu4k.kontomatikchallenge.utils.JsonUtils;
import michu4k.kontomatikchallenge.utils.UrlProvider;
import michu4k.kontomatikchallenge.utils.bankaccountstools.JsonBankAccountsExtractor;
import michu4k.kontomatikchallenge.utils.factories.WebRequestFactory;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;

import javax.json.JsonArray;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class NestBankAccountScraper implements BankAccountScraper {
    private final WebClient webClient;

    public NestBankAccountScraper(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<BankAccount> scrapeBankAccounts(BankSession bankSession) throws IOException {
        WebRequest bankAccountsRequest = createBankAccountRequest(bankSession);
        String bankAccountResponse = sendBankAccountsRequest(bankAccountsRequest);
        JsonArray jsonBankAccounts = parseBankAccountsResponseToJsonArray(bankAccountResponse);
        return JsonBankAccountsExtractor.extractBankAccountsFromJsonArray(jsonBankAccounts);
    }

    private WebRequest createBankAccountRequest(BankSession bankSession) throws IOException {
        URL bankAccountsUrl = new URL(UrlProvider.BANK_ACCOUNTS_SITE_URL_BEGINNING + bankSession.userId + UrlProvider.BANK_ACCOUNTS_SITE_URL_END);
        return WebRequestFactory.createRequestGet(bankAccountsUrl, bankSession.sessionToken);
    }

    private String sendBankAccountsRequest(WebRequest bankAccountsRequest) throws IOException {
        Page bankAccountsPage = webClient.getPage(bankAccountsRequest);
        return bankAccountsPage.getWebResponse().getContentAsString();
    }

    private JsonArray parseBankAccountsResponseToJsonArray(String bankAccountsResponse) {
        return JsonUtils.parseStringToJsonArray(bankAccountsResponse, "accounts");
    }
}