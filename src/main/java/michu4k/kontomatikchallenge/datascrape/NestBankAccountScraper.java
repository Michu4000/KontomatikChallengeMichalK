package michu4k.kontomatikchallenge.datascrape;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;

import michu4k.kontomatikchallenge.datastructures.BankAccount;
import michu4k.kontomatikchallenge.datastructures.BankSession;
import michu4k.kontomatikchallenge.utils.WebRequestFactory;

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

    //TODO method name
    @Override
    public List<BankAccount> scrapeBankAccounts(BankSession bankSession) throws IOException {

        //TODO don't mix up different abstraction levels!

        //TODO String concat (?)
        //TODO DON'T USE WORD DATA!
        URL bankAccountsDataUrl = new URL(new StringBuilder(BANK_ACCOUNTS_DATA_SITE_URL_BEGINNING)
                    .append(bankSession.userId)
                    .append(BANK_ACCOUNTS_DATA_SITE_URL_END)
                    .toString());

        //TODO DON'T USE WORD DATA!
        WebRequest checkBankAccountsDataRequest = WebRequestFactory.produceRequestGet(bankAccountsDataUrl,
                bankSession.sessionToken);

        //TODO DON'T USE WORD DATA!
        Page bankAccountsDataPage;
        //TODO catch it higher
        //TODO wrap exception not only stacktrace

        bankAccountsDataPage = webClient.getPage(checkBankAccountsDataRequest);

        //TODO DON'T USE WORD DATA!
        String checkBankAccountsDataResponse = bankAccountsDataPage.getWebResponse().getContentAsString();
        //TODO DON'T USE WORD INFO!
        List<String> bankAccountsInfoList = readBankAccountsInfoFromResponse(checkBankAccountsDataResponse);
        return extractBankAccountsDataFromBankAccountsInfoList(bankAccountsInfoList);
    }

    //TODO DON'T USE WORD INFO!
    //TODO method name too long
    private List<String> readBankAccountsInfoFromResponse(String response) throws IOException {
        //TODO use library to build JSONs
        Pattern bankAccountsPattern = Pattern.compile("\"accounts\":\\[(.*)\\],\"savingsAccounts\"");
        Matcher bankAccountsMatcher = bankAccountsPattern.matcher(response);
        String bankAccountsInfo;
        if (bankAccountsMatcher.find())
            bankAccountsInfo = bankAccountsMatcher.group(1);
        else
            throw new IOException();

        return Arrays.asList(bankAccountsInfo.split("\\},"));
    }

    //TODO too long method (?)
    //TODO DON'T USE WORDs DATA and INFO!
    //TODO method name too long
    private List<BankAccount> extractBankAccountsDataFromBankAccountsInfoList(List<String> bankAccountsInfoList)
            throws IOException {
        //TODO DON'T USE WORD DATA!
        List<BankAccount> bankAccountsData = new ArrayList<>();

        //TODO use library to build JSONs
        Pattern bankAccountsNamesPattern = Pattern.compile("\"name\":\"(.*)\",\"openingBalance\"");
        Pattern bankAccountsNumbersPattern = Pattern.compile("\"nrb\":\"(.*)\",\"name\"");
        Pattern bankAccountsBalancesPattern = Pattern.compile("\"balance\":(.*),\"balanceDate\"");
        Pattern bankAccountsCurrenciesPattern = Pattern.compile("\"currency\":\"(.*)\",\"version\"");

        //TODO DON'T USE WORD INFO!
        for (String bankAccountInfo : bankAccountsInfoList) {
            //TODO DON'T USE WORD DATA!
            BankAccount bankAccount = new BankAccount();

            Matcher bankAccountsNamesMatcher = bankAccountsNamesPattern.matcher(bankAccountInfo);
            Matcher bankAccountsNumbersMatcher = bankAccountsNumbersPattern.matcher(bankAccountInfo);
            Matcher bankAccountsBalancesMatcher = bankAccountsBalancesPattern.matcher(bankAccountInfo);
            Matcher bankAccountsCurrenciesMatcher = bankAccountsCurrenciesPattern.matcher(bankAccountInfo);

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

            bankAccountsData.add(bankAccount);
        }
        return bankAccountsData;
    }
}
