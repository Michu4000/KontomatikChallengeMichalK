package michu4k.kontomatikchallenge.datascrape;

import michu4k.kontomatikchallenge.datastructures.BankAccount;
import michu4k.kontomatikchallenge.datastructures.BankSession;
import michu4k.kontomatikchallenge.utils.WebRequestFactory;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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

        WebRequest checkBankAccountsRequest = WebRequestFactory.produceRequestGet(bankAccountsUrl,
                bankSession.sessionToken);

        Page bankAccountsPage = webClient.getPage(checkBankAccountsRequest);

        String checkBankAccountsResponse = bankAccountsPage.getWebResponse().getContentAsString();
        List<String> rawBankAccountsList = readRawBankAccountsFromResponse(checkBankAccountsResponse);
        return extractBankAccountsFromRawBankAccountsList(rawBankAccountsList);
    }

    //TODO method name too long
    private List<String> readRawBankAccountsFromResponse(String response) throws IOException {
        //TODO use library to parse JSONs
        Pattern bankAccountsPattern = Pattern.compile("\"accounts\":\\[(.*)\\],\"savingsAccounts\"");
        Matcher bankAccountsMatcher = bankAccountsPattern.matcher(response);
        String rawBankAccounts;
        if (bankAccountsMatcher.find())
            rawBankAccounts = bankAccountsMatcher.group(1);
        else
            throw new IOException();

        return Arrays.asList(rawBankAccounts.split("\\},"));
    }

    //TODO too long method (?)
    //TODO method name too long
    private List<BankAccount> extractBankAccountsFromRawBankAccountsList(List<String> rawBankAccountsList)
            throws IOException {
        List<BankAccount> bankAccounts = new ArrayList<>();

        //TODO use library to parse JSONs
        Pattern bankAccountsNamesPattern = Pattern.compile("\"name\":\"(.*)\",\"openingBalance\"");
        Pattern bankAccountsNumbersPattern = Pattern.compile("\"nrb\":\"(.*)\",\"name\"");
        Pattern bankAccountsBalancesPattern = Pattern.compile("\"balance\":(.*),\"balanceDate\"");
        Pattern bankAccountsCurrenciesPattern = Pattern.compile("\"currency\":\"(.*)\",\"version\"");

        for (String rawBankAccount : rawBankAccountsList) {
            BankAccount bankAccount = new BankAccount();

            Matcher bankAccountsNamesMatcher = bankAccountsNamesPattern.matcher(rawBankAccount);
            Matcher bankAccountsNumbersMatcher = bankAccountsNumbersPattern.matcher(rawBankAccount);
            Matcher bankAccountsBalancesMatcher = bankAccountsBalancesPattern.matcher(rawBankAccount);
            Matcher bankAccountsCurrenciesMatcher = bankAccountsCurrenciesPattern.matcher(rawBankAccount);

            if (bankAccountsNamesMatcher.find())
                bankAccount.accountName = bankAccountsNamesMatcher.group(1);
            else
                throw new IOException();

            if (bankAccountsNumbersMatcher.find()) {
                Stream<String> bankAccountNumberStream = Arrays.stream(bankAccountsNumbersMatcher.group(1).split(""));
                bankAccount.accountNumber = bankAccountNumberStream.mapToInt(x -> Integer.parseInt(x)).toArray();
            } else {
                throw new IOException();
            }

            if (bankAccountsBalancesMatcher.find())
                bankAccount.accountBalance = new BigDecimal(bankAccountsBalancesMatcher.group(1));
            else
                throw new IOException();

            if (bankAccountsCurrenciesMatcher.find())
                bankAccount.accountCurrency = bankAccountsCurrenciesMatcher.group(1);
            else
                throw new IOException();

            bankAccounts.add(bankAccount);
        }
        return bankAccounts;
    }
}
