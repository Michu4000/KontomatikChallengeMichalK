package michu4k.kontomatikchallenge.datascrape;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;

import michu4k.kontomatikchallenge.userinterface.UserInterface;
import michu4k.kontomatikchallenge.utils.WebRequestFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleAccountScraper implements AccountScraper {
    private final static String ACCOUNTS_STATUS_SITE_URL_BEGINNING = "https://login.nestbank.pl/rest/v1/context/";
    private final static String ACCOUNTS_STATUS_SITE_URL_END = "/dashboard/www/config";

    private WebClient webClient;
    private String sessionToken, userId;
    private List<String> accountsNames;
    private List<String> accountsNumbers;
    private List<String> accountsBalances;
    private List<String> accountsCurrencies;

    public SimpleAccountScraper(String sessionToken, String userId, WebClient webClient) {
        this.sessionToken = sessionToken;
        this.userId = userId;
        this.webClient = webClient;
    }

    @Override
    public void scrapeAccounts() {
        URL accountsStatusUrl = null;
        try {
            accountsStatusUrl = new URL(new StringBuilder(ACCOUNTS_STATUS_SITE_URL_BEGINNING)
                    .append(userId)
                    .append(ACCOUNTS_STATUS_SITE_URL_END)
                    .toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        WebRequest checkAccountsStatusRequest = WebRequestFactory.produceRequestGet(accountsStatusUrl,
                sessionToken);

        Page accountsStatusPage = null;
        try {
            accountsStatusPage = webClient.getPage(checkAccountsStatusRequest);
        } catch (IOException e) {
            e.printStackTrace();
            UserInterface.printConnectionError();
            System.exit(1);
        }
        String checkAccountsStatusResponse = accountsStatusPage.getWebResponse().getContentAsString();
        List<String> accountsInfoList = readAccountsInfoFromResponse(checkAccountsStatusResponse);
        extractAccountsDatafromAccountsInfoList(accountsInfoList);
    }

    private List<String> readAccountsInfoFromResponse(String response) {
        Pattern accountsPattern = Pattern.compile("\"accounts\":\\[(.*)\\],\"savingsAccounts\"");
        Matcher accountsMatcher = accountsPattern.matcher(response);
        String accountsInfo = null;
        if (accountsMatcher.find()) {
            accountsInfo = accountsMatcher.group(1);
        }
        else {
            UserInterface.printConnectionError();
            System.exit(1);
        }
        return Arrays.asList(accountsInfo.split("\\},"));
    }

    private void extractAccountsDatafromAccountsInfoList(List<String> accountsInfoList) {
        accountsNames = new ArrayList();
        Pattern accountsNamesPattern = Pattern.compile("\"name\":\"(.*)\",\"openingBalance\"");
        accountsNumbers = new ArrayList();
        Pattern accountsNumbersPattern = Pattern.compile("\"nrb\":\"(.*)\",\"name\"");
        accountsBalances = new ArrayList();
        Pattern accountsBalancesPattern = Pattern.compile("\"balance\":(.*),\"balanceDate\"");
        accountsCurrencies = new ArrayList();
        Pattern accountsCurrenciesPattern = Pattern.compile("\"currency\":\"(.*)\",\"version\"");

        for (String accountInfo : accountsInfoList) {
            Matcher accountsNamesMatcher = accountsNamesPattern.matcher(accountInfo);
            Matcher accountsNumbersMatcher = accountsNumbersPattern.matcher(accountInfo);
            Matcher accountsBalancesMatcher = accountsBalancesPattern.matcher(accountInfo);
            Matcher accountsCurrenciesMatcher = accountsCurrenciesPattern.matcher(accountInfo);

            if (accountsNamesMatcher.find()) {
                accountsNames.add(accountsNamesMatcher.group(1));
            }
            else {
                UserInterface.printConnectionError();
                System.exit(1);
            }

            if (accountsNumbersMatcher.find()) {
                accountsNumbers.add(accountsNumbersMatcher.group(1));
            }
            else {
                UserInterface.printConnectionError();
                System.exit(1);
            }

            if (accountsBalancesMatcher.find()) {
                accountsBalances.add(accountsBalancesMatcher.group(1));
            }
            else {
                UserInterface.printConnectionError();
                System.exit(1);
            }

            if (accountsCurrenciesMatcher.find()) {
                accountsCurrencies.add(accountsCurrenciesMatcher.group(1));
            }
            else {
                UserInterface.printConnectionError();
                System.exit(1);
            }
        }
    }

    @Override
    public List<String> getAccountsNames() {
        return accountsNames;
    }

    @Override
    public List<String> getAccountsNumbers() {
        return accountsNumbers;
    }

    @Override
    public List<String> getAccountsBalances() {
        return accountsBalances;
    }

    @Override
    public List<String> getAccountsCurrencies() {
        return accountsCurrencies;
    }
}
