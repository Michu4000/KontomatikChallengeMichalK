package michu4k.kontomatikchallenge.datascrape;

import michu4k.kontomatikchallenge.datastructures.BankAccount;
import michu4k.kontomatikchallenge.datastructures.BankSession;
import michu4k.kontomatikchallenge.utils.WebRequestFactory;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;

import javax.json.JsonObject;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonReader;
import javax.json.JsonArray;
import javax.json.JsonValue;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NestBankAccountScraper implements BankAccountScraper {
    private final static String BANK_ACCOUNTS_DATA_SITE_URL_BEGINNING = "https://login.nestbank.pl/rest/v1/context/";
    private final static String BANK_ACCOUNTS_DATA_SITE_URL_END = "/dashboard/www/config";

    private final WebClient webClient;
    private String bankAccountsResponse;
    private JsonArray jsonBankAccounts;

    public NestBankAccountScraper(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<BankAccount> scrapeBankAccounts(BankSession bankSession) throws IOException {
        sendBankAccountsRequest(bankSession);

        try {
            parseResponseToJsonArray();
            return extractBankAccountsFromJsonArray();
        } catch (JsonException | IllegalStateException | ClassCastException jsonException) {
            throw new IOException(jsonException);
        }
    }

    private void sendBankAccountsRequest(BankSession bankSession) throws IOException {
        URL bankAccountsUrl = new URL(
                BANK_ACCOUNTS_DATA_SITE_URL_BEGINNING
                        + bankSession.userId
                        + BANK_ACCOUNTS_DATA_SITE_URL_END
        );
        WebRequest bankAccountsRequest = WebRequestFactory.createRequestGet(bankAccountsUrl, bankSession.sessionToken);
        Page bankAccountsPage = webClient.getPage(bankAccountsRequest);
        bankAccountsResponse = bankAccountsPage.getWebResponse().getContentAsString();
    }

    private void parseResponseToJsonArray() {
        JsonReader jsonReader = Json.createReader(new StringReader(bankAccountsResponse));
        JsonObject bankAccountsJson = jsonReader.readObject();
        jsonBankAccounts = bankAccountsJson.getJsonArray("accounts");
    }

    private List<BankAccount> extractBankAccountsFromJsonArray() {
        List<BankAccount> bankAccounts = new ArrayList<>();
        for (JsonValue jsonBankAccount : jsonBankAccounts) {
            BankAccount bankAccount = new BankAccount();
            bankAccount.accountName = jsonBankAccount.asJsonObject().getString("name");
            bankAccount.accountNumber = Arrays.stream(
                    jsonBankAccount.asJsonObject().getString("nrb").split("")
            ).mapToInt(Integer::parseInt).toArray();
            bankAccount.accountBalance = jsonBankAccount.asJsonObject().getJsonNumber("balance").bigDecimalValue();
            bankAccount.accountCurrency = jsonBankAccount.asJsonObject().getString("currency");
            bankAccounts.add(bankAccount);
        }
        return bankAccounts;
    }
}