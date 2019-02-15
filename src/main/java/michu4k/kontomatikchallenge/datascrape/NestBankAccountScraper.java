package michu4k.kontomatikchallenge.datascrape;

import michu4k.kontomatikchallenge.datastructures.BankAccount;
import michu4k.kontomatikchallenge.datastructures.BankSession;
import michu4k.kontomatikchallenge.utils.JsonBankAccountsExtractor;
import michu4k.kontomatikchallenge.utils.JsonUtils;
import michu4k.kontomatikchallenge.utils.UrlProvider;
import michu4k.kontomatikchallenge.utils.WebRequestFactory;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;

import javax.json.JsonException;
import javax.json.JsonArray;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class NestBankAccountScraper implements BankAccountScraper {
    private final WebClient webClient;
    private WebRequest bankAccountsRequest;
    private String bankAccountsResponse;
    private JsonArray jsonBankAccounts;

    public NestBankAccountScraper(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<BankAccount> scrapeBankAccounts(BankSession bankSession) throws IOException {
        createBankAccountRequest(bankSession);
        sendBankAccountsRequest();
        parseBankAccountsResponseToJsonArray();
        return JsonBankAccountsExtractor.extractBankAccountsFromJsonArray(jsonBankAccounts);
    }

    private void createBankAccountRequest(BankSession bankSession) throws IOException {
        URL bankAccountsUrl = new URL(
                UrlProvider.BANK_ACCOUNTS_SITE_URL_BEGINNING
                        + bankSession.userId
                        + UrlProvider.BANK_ACCOUNTS_SITE_URL_END
        );
        bankAccountsRequest = WebRequestFactory.createRequestGet(bankAccountsUrl, bankSession.sessionToken);
    }

    private void sendBankAccountsRequest() throws IOException {
        Page bankAccountsPage = webClient.getPage(bankAccountsRequest);
        bankAccountsResponse = bankAccountsPage.getWebResponse().getContentAsString();
    }

    private void parseBankAccountsResponseToJsonArray() throws IOException {
        try {
            jsonBankAccounts = JsonUtils.parseStringToJsonArray(bankAccountsResponse, "accounts");
        } catch (JsonException | IllegalStateException | ClassCastException | NullPointerException jsonException) {
            throw new IOException(jsonException);
        }
    }
}