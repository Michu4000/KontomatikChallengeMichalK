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

    public NestBankAccountScraper(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<BankAccount> scrapeBankAccounts(BankSession bankSession) throws IOException {

        //TODO don't mix up different abstraction levels!

        URL bankAccountsUrl = new URL(BANK_ACCOUNTS_DATA_SITE_URL_BEGINNING + bankSession.userId + BANK_ACCOUNTS_DATA_SITE_URL_END);

        WebRequest checkBankAccountsRequest = WebRequestFactory.createRequestGet(bankAccountsUrl,
                bankSession.sessionToken);

        Page bankAccountsPage = webClient.getPage(checkBankAccountsRequest);

        String checkBankAccountsResponse = bankAccountsPage.getWebResponse().getContentAsString();
        JsonArray rawBankAccountsList = readRawBankAccountsFromResponse(checkBankAccountsResponse);
        return extractBankAccountsFromRawBankAccountsList(rawBankAccountsList);
    }

    //TODO method name too long
    private JsonArray readRawBankAccountsFromResponse(String response) throws IOException {
        JsonReader reader = Json.createReader(new StringReader(response));
        JsonArray bankAccountsJsonArray;
        try {
            JsonObject bankAccountsJson = reader.readObject();
            bankAccountsJsonArray = bankAccountsJson.getJsonArray("accounts");
        } catch (JsonException | IllegalStateException | ClassCastException jsonException) {
            throw new IOException(jsonException);
        }
        return bankAccountsJsonArray;
    }

    //TODO method name too long
    private List<BankAccount> extractBankAccountsFromRawBankAccountsList(JsonArray rawBankAccountsList)
            throws IOException {
        List<BankAccount> bankAccounts = new ArrayList<>();
        for (JsonValue rawBankAccount : rawBankAccountsList) {
            BankAccount bankAccount = new BankAccount();
            try {
                bankAccount.accountName = rawBankAccount.asJsonObject().getString("name");
                bankAccount.accountNumber = Arrays.stream(
                        rawBankAccount.asJsonObject().getString("nrb").split("")
                ).mapToInt(Integer::parseInt).toArray();
                bankAccount.accountBalance = rawBankAccount.asJsonObject().getJsonNumber("balance").bigDecimalValue();
                bankAccount.accountCurrency = rawBankAccount.asJsonObject().getString("currency");
                bankAccounts.add(bankAccount);
            } catch (ClassCastException | NumberFormatException jsonException) {
                throw new IOException(jsonException);
            }
        }
        return bankAccounts;
    }
}