package michu4k.kontomatikchallenge.datascrape;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;

import michu4k.kontomatikchallenge.datastructures.BankAccountData;
import michu4k.kontomatikchallenge.datastructures.BankSession;
import michu4k.kontomatikchallenge.exceptions.BankConnectionException;
import michu4k.kontomatikchallenge.utils.WebRequestFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
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

    private WebClient webClient;

    public NestBankAccountScraper(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<BankAccountData> scrapeBankAccounts(BankSession bankSession) throws BankConnectionException {
        URL bankAccountsDataUrl = null;
        //TODO throw this exception higher, then catch and show internal application error etc.
        try {
            bankAccountsDataUrl = new URL(new StringBuilder(BANK_ACCOUNTS_DATA_SITE_URL_BEGINNING)
                    .append(bankSession.getUserId())
                    .append(BANK_ACCOUNTS_DATA_SITE_URL_END)
                    .toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        WebRequest checkBankAccountsDataRequest = WebRequestFactory.produceRequestGet(bankAccountsDataUrl,
                bankSession.getSessionToken());

        Page bankAccountsDataPage;
        try {
            bankAccountsDataPage = webClient.getPage(checkBankAccountsDataRequest);
        } catch (IOException e) {
            e.printStackTrace(); //TODO don't print stack trace, include it in new exception
            throw new BankConnectionException();
        }
        String checkBankAccountsDataResponse = bankAccountsDataPage.getWebResponse().getContentAsString();
        List<String> bankAccountsInfoList = readBankAccountsInfoFromResponse(checkBankAccountsDataResponse);
        return extractBankAccountsDatafromBankAccountsInfoList(bankAccountsInfoList);
    }

    private List<String> readBankAccountsInfoFromResponse(String response) throws BankConnectionException {
        Pattern bankAccountsPattern = Pattern.compile("\"accounts\":\\[(.*)\\],\"savingsAccounts\"");
        Matcher bankAccountsMatcher = bankAccountsPattern.matcher(response);
        String bankAccountsInfo;
        if (bankAccountsMatcher.find()) {
            bankAccountsInfo = bankAccountsMatcher.group(1);
        }
        else {
            throw new BankConnectionException();
        }
        return Arrays.asList(bankAccountsInfo.split("\\},"));
    }

    private List<BankAccountData> extractBankAccountsDatafromBankAccountsInfoList(List<String> bankAccountsInfoList)
            throws BankConnectionException {
        List<BankAccountData> bankAccountsData = new ArrayList<>();

        Pattern bankAccountsNamesPattern = Pattern.compile("\"name\":\"(.*)\",\"openingBalance\"");
        Pattern bankAccountsNumbersPattern = Pattern.compile("\"nrb\":\"(.*)\",\"name\"");
        Pattern bankAccountsBalancesPattern = Pattern.compile("\"balance\":(.*),\"balanceDate\"");
        Pattern bankAccountsCurrenciesPattern = Pattern.compile("\"currency\":\"(.*)\",\"version\"");

        for (String bankAccountInfo : bankAccountsInfoList) {
            BankAccountData bankAccount = new BankAccountData();

            Matcher bankAccountsNamesMatcher = bankAccountsNamesPattern.matcher(bankAccountInfo);
            Matcher bankAccountsNumbersMatcher = bankAccountsNumbersPattern.matcher(bankAccountInfo);
            Matcher bankAccountsBalancesMatcher = bankAccountsBalancesPattern.matcher(bankAccountInfo);
            Matcher bankAccountsCurrenciesMatcher = bankAccountsCurrenciesPattern.matcher(bankAccountInfo);

            if (bankAccountsNamesMatcher.find()) {
                bankAccount.setAccountName(bankAccountsNamesMatcher.group(1));
            }
            else {
                throw new BankConnectionException();
            }

            if (bankAccountsNumbersMatcher.find()) {
                Stream<String> bankAccountNumberStream = Arrays.stream(bankAccountsNumbersMatcher.group(1).split(""));
                try {
                    int[] bankAccountNumberIntArr = bankAccountNumberStream.mapToInt(x -> Integer.parseInt(x)).toArray();
                    bankAccount.setAccountNumber(bankAccountNumberIntArr);
                } catch (NumberFormatException e) {
                    //TODO include stack trace in new exception
                    throw new BankConnectionException();
                }
            }
            else {
                throw new BankConnectionException();
            }

            if (bankAccountsBalancesMatcher.find()) {
                bankAccount.setAccountBalance(new BigDecimal(bankAccountsBalancesMatcher.group(1)));
            }
            else {
                throw new BankConnectionException();
            }

            if (bankAccountsCurrenciesMatcher.find()) {
                bankAccount.setAccountCurrency(bankAccountsCurrenciesMatcher.group(1));
            }
            else {
                throw new BankConnectionException();
            }

            bankAccountsData.add(bankAccount);
        }

        return bankAccountsData;
    }
}
