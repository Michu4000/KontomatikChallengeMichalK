package michu4k.kontomatikchallenge.datascrape;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;

import michu4k.kontomatikchallenge.datastructures.BankAccountData;
import michu4k.kontomatikchallenge.datastructures.BankSession;
import michu4k.kontomatikchallenge.userinterface.ErrorsPrinter;
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

public class SimpleBankAccountScraper implements BankAccountScraper {
    private final static String ACCOUNTS_STATUS_SITE_URL_BEGINNING = "https://login.nestbank.pl/rest/v1/context/";
    private final static String ACCOUNTS_STATUS_SITE_URL_END = "/dashboard/www/config";

    private WebClient webClient;
    private BankSession bankSession;
    private List<BankAccountData> bankAccountsData;

    public SimpleBankAccountScraper(BankSession bankSession, WebClient webClient) {
        this.bankSession = bankSession;
        this.webClient = webClient;
    }

    @Override
    public List<BankAccountData> scrapeAccounts() {
        URL accountsStatusUrl = null;
        try {
            accountsStatusUrl = new URL(new StringBuilder(ACCOUNTS_STATUS_SITE_URL_BEGINNING)
                    .append(bankSession.getUserId())
                    .append(ACCOUNTS_STATUS_SITE_URL_END)
                    .toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        WebRequest checkAccountsStatusRequest = WebRequestFactory.produceRequestGet(accountsStatusUrl,
                bankSession.getSessionToken());

        Page accountsStatusPage = null;
        try {
            accountsStatusPage = webClient.getPage(checkAccountsStatusRequest);
        } catch (IOException e) {
            e.printStackTrace();
            ErrorsPrinter.printConnectionError();
            System.exit(1);
        }
        String checkAccountsStatusResponse = accountsStatusPage.getWebResponse().getContentAsString();
        List<String> accountsInfoList = readAccountsInfoFromResponse(checkAccountsStatusResponse);
        extractAccountsDatafromAccountsInfoList(accountsInfoList);
        return bankAccountsData;
    }

    private List<String> readAccountsInfoFromResponse(String response) {
        Pattern accountsPattern = Pattern.compile("\"accounts\":\\[(.*)\\],\"savingsAccounts\"");
        Matcher accountsMatcher = accountsPattern.matcher(response);
        String accountsInfo = null;
        if (accountsMatcher.find()) {
            accountsInfo = accountsMatcher.group(1);
        }
        else {
            ErrorsPrinter.printConnectionError();
            System.exit(1);
        }
        return Arrays.asList(accountsInfo.split("\\},"));
    }

    private void extractAccountsDatafromAccountsInfoList(List<String> accountsInfoList) {
        bankAccountsData = new ArrayList<BankAccountData>();

        Pattern accountsNamesPattern = Pattern.compile("\"name\":\"(.*)\",\"openingBalance\"");
        Pattern accountsNumbersPattern = Pattern.compile("\"nrb\":\"(.*)\",\"name\"");
        Pattern accountsBalancesPattern = Pattern.compile("\"balance\":(.*),\"balanceDate\"");
        Pattern accountsCurrenciesPattern = Pattern.compile("\"currency\":\"(.*)\",\"version\"");

        for (String accountInfo : accountsInfoList) {
            BankAccountData bankAccount = new BankAccountData();

            Matcher accountsNamesMatcher = accountsNamesPattern.matcher(accountInfo);
            Matcher accountsNumbersMatcher = accountsNumbersPattern.matcher(accountInfo);
            Matcher accountsBalancesMatcher = accountsBalancesPattern.matcher(accountInfo);
            Matcher accountsCurrenciesMatcher = accountsCurrenciesPattern.matcher(accountInfo);

            if (accountsNamesMatcher.find()) {
                bankAccount.setAccountName(accountsNamesMatcher.group(1));
            }
            else {
                ErrorsPrinter.printConnectionError();
                System.exit(1);
            }

            if (accountsNumbersMatcher.find()) {
                Stream<String> accountNumberStream = Arrays.stream(accountsNumbersMatcher.group(1).split(""));
                int[] accountNumberIntArr = accountNumberStream.mapToInt(x -> Integer.parseInt(x)).toArray();
                bankAccount.setAccountNumber(accountNumberIntArr);
            }
            else {
                ErrorsPrinter.printConnectionError();
                System.exit(1);
            }

            if (accountsBalancesMatcher.find()) {
                bankAccount.setAccountBalance(new BigDecimal(accountsBalancesMatcher.group(1)));
            }
            else {
                ErrorsPrinter.printConnectionError();
                System.exit(1);
            }

            if (accountsCurrenciesMatcher.find()) {
                bankAccount.setAccountCurrency(accountsCurrenciesMatcher.group(1));
            }
            else {
                ErrorsPrinter.printConnectionError();
                System.exit(1);
            }

            bankAccountsData.add(bankAccount);
        }
    }
}
