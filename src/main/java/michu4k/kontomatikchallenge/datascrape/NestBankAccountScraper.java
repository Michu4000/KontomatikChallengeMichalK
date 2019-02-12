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
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class NestBankAccountScraper implements BankAccountScraper {
    private final static String BANK_ACCOUNTS_DATA_SITE_URL_BEGINNING = "https://login.nestbank.pl/rest/v1/context/";
    private final static String BANK_ACCOUNTS_DATA_SITE_URL_END = "/dashboard/www/config";

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
        try {
            parseBankAccountsResponseToJsonArray();
            return extractBankAccountsFromJsonArray();
        } catch (JsonException | IllegalStateException | ClassCastException | NullPointerException jsonException) {
            throw new IOException(jsonException);
        }
    }

    private void createBankAccountRequest(BankSession bankSession) throws IOException {
        URL bankAccountsUrl = new URL(
                BANK_ACCOUNTS_DATA_SITE_URL_BEGINNING
                        + bankSession.userId
                        + BANK_ACCOUNTS_DATA_SITE_URL_END
        );
        bankAccountsRequest = WebRequestFactory.createRequestGet(bankAccountsUrl, bankSession.sessionToken);
    }

    private void sendBankAccountsRequest() throws IOException {
        Page bankAccountsPage = webClient.getPage(bankAccountsRequest);
        bankAccountsResponse = bankAccountsPage.getWebResponse().getContentAsString();
    }

    private void parseBankAccountsResponseToJsonArray() {
        JsonReader jsonReader = Json.createReader(new StringReader(bankAccountsResponse));
        JsonObject rawJsonBankAccounts = jsonReader.readObject();
        jsonBankAccounts = rawJsonBankAccounts.getJsonArray("accounts");
    }

    private List<BankAccount> extractBankAccountsFromJsonArray() {
        List<BankAccount> bankAccountsGroup = new ArrayList<>();
        for (JsonValue jsonBankAccount : jsonBankAccounts) {
            BankAccount bankAccount = new BankAccount();
            bankAccount.accountName = JsonBankAccountExtractor.extractName(jsonBankAccount);
            bankAccount.accountNumber = JsonBankAccountExtractor.extractNumber(jsonBankAccount);
            bankAccount.accountBalance = JsonBankAccountExtractor.extractBalance(jsonBankAccount);
            bankAccount.accountCurrency = JsonBankAccountExtractor.extractCurrency(jsonBankAccount);
            bankAccountsGroup.add(bankAccount);
        }
        return bankAccountsGroup;
    }

    private static class JsonBankAccountExtractor {
        private static String extractName(JsonValue jsonBankAccount) {
            return jsonBankAccount.asJsonObject().getString("name");
        }

        private static int[] extractNumber(JsonValue jsonBankAccount) {
            Stream<String> bankAccountNumberStream = Arrays.stream(
                    jsonBankAccount.asJsonObject().getString("nrb").split("")
            );
            return bankAccountNumberStream.mapToInt(Integer::parseInt).toArray();
        }

        private static BigDecimal extractBalance(JsonValue jsonBankAccount) {
            return jsonBankAccount.asJsonObject().getJsonNumber("balance").bigDecimalValue();
        }

        private static String extractCurrency(JsonValue jsonBankAccount) {
            return jsonBankAccount.asJsonObject().getString("currency");
        }
    }
}