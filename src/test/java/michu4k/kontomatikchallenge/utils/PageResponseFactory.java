package michu4k.kontomatikchallenge.utils;

import com.gargoylesoftware.htmlunit.Page;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import michu4k.kontomatikchallenge.stubs.PageStub;

public class PageResponseFactory {
    public static Page getPageBadLogin() throws IOException {
        String responseBody = "{\"level\":\"ERROR\",\"problems\":[{\"level\":\"ERROR\",\"objectId\":null," +
                "\"objectIdsList\":[],\"objectName\":null,\"messageParams\":null,\"propertyName\":\"login\"," +
                "\"propertyValue\":null,\"messageCode\":\"login.incorrect\",\"locale\":\"pl_PL\"," +
                "\"description\":\"login.incorrect\"}]}";
        String urlAddress = "https://login.nestbank.pl/rest/v1/auth/checkLogin";
        return getPage(urlAddress, new HashMap<>(), responseBody);
    }

    public static Page getPageValidLogin(boolean maskedPasswordMethod, int passwordLength, int[] maskedPasswordKeysIndexes)
            throws IOException {
        String responseBody;
        if(maskedPasswordMethod) {
            responseBody =
                    "{\"avatars\":[{\"id\":21,\"url\":\"/rest/resource?id=21\",\"name\":\"czapka\"}," +
                            "{\"id\":23,\"url\":\"/rest/resource?id=23\",\"name\":\"rakietka\"}," +
                            "{\"id\":22,\"url\":\"/rest/resource?id=22\",\"name\":\"śnieg\"}," +
                            "{\"id\":20,\"url\":\"/rest/resource?id=20\",\"name\":\"rolki\"}," +
                            "{\"id\":11,\"url\":\"/rest/resource?id=11\",\"name\":\"kot\"}," +
                            "{\"id\":16,\"url\":\"/rest/resource?id=16\",\"name\":\"korona\"}," +
                            "{\"id\":4,\"url\":\"/rest/resource?id=4\",\"name\":\"doniczka\"}," +
                            "{\"id\":10,\"url\":\"/rest/resource?id=10\",\"name\":\"miś\"}," +
                            "{\"id\":18,\"url\":\"/rest/resource?id=18\",\"name\":\"tort\"}," +
                            "{\"id\":9,\"url\":\"/rest/resource?id=9\",\"name\":\"rower\"}," +
                            "{\"id\":2,\"url\":\"/rest/resource?id=2\",\"name\":\"ptak\"}," +
                            "{\"id\":15,\"url\":\"/rest/resource?id=15\",\"name\":\"ryba\"}]," +
                            "\"passwordLength\":" + passwordLength + ",\"passwordKeys\":" +
                            Arrays.toString(maskedPasswordKeysIndexes) + ",\"loginProcess\":\"PARTIAL_PASSWORD\"}";
        } else {
            responseBody = "{\"avatars\":[{\"id\":2,\"url\":\"/rest/resource?id=2\",\"name\":\"ptak\"}," +
                    "{\"id\":3,\"url\":\"/rest/resource?id=3\",\"name\":\"prezent\"}," +
                    "{\"id\":20,\"url\":\"/rest/resource?id=20\",\"name\":\"rolki\"}," +
                    "{\"id\":8,\"url\":\"/rest/resource?id=8\",\"name\":\"jacht\"}," +
                    "{\"id\":22,\"url\":\"/rest/resource?id=22\",\"name\":\"śnieg\"}," +
                    "{\"id\":14,\"url\":\"/rest/resource?id=14\",\"name\":\"but\"}," +
                    "{\"id\":5,\"url\":\"/rest/resource?id=5\",\"name\":\"drzewo\"}," +
                    "{\"id\":9,\"url\":\"/rest/resource?id=9\",\"name\":\"rower\"}," +
                    "{\"id\":11,\"url\":\"/rest/resource?id=11\",\"name\":\"kot\"}," +
                    "{\"id\":17,\"url\":\"/rest/resource?id=17\",\"name\":\"delfin\"}," +
                    "{\"id\":15,\"url\":\"/rest/resource?id=15\",\"name\":\"ryba\"}," +
                    "{\"id\":7,\"url\":\"/rest/resource?id=7\",\"name\":\"ster\"}],\"loginProcess\":\"FULL_PASSWORD\"}";
        }
        String urlAddress = "https://login.nestbank.pl/rest/v1/auth/checkLogin";
        return getPage(urlAddress, new HashMap<>(), responseBody);
    }

    public static Page getPageValidPasswordAndAvatar(String sessionToken, int userId) throws IOException {
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Session-Token", sessionToken);

        String responseBody = "{\"userContexts\":[{\"id\":" + userId +
                ",\"name\":\"ADAM NOWAK\",\"type\":\"PRIVATE\"," +
                "\"customerPesel\":null,\"customerRegon\":null,\"customerNip\":null,\"address\":null," +
                "\"customerId\":96224831,\"selected\":true,\"cf\":false},{\"id\":48705044," +
                "\"name\":\"ADAM NOWAK\",\"type\":\"CORPORATE\",\"customerPesel\":null,\"customerRegon\":null," +
                "\"customerNip\":null,\"address\":null,\"customerId\":128652005,\"selected\":false,\"cf\":false}]," +
                "\"lastSuccessLoginDate\":\"2019-02-13 12:00:13\",\"authorizationPasswordShouldBeSet\":false," +
                "\"authorizationPasswordEnabledFrom\":null,\"userShouldSetAuthorizationToken\":false," +
                "\"sessionTimeoutValue\":5}";

        String urlAddress = "https://login.nestbank.pl/rest/v1/auth/loginByPartialPassword";
        return getPage(urlAddress, responseHeaders, responseBody);
    }

    public static Page getPageBadPasswordAndAvatar() throws IOException {
        String responseBody = "{\"level\":\"ERROR\",\"problems\":[{\"level\":\"ERROR\",\"objectId\":null," +
                "\"objectIdsList\":[],\"objectName\":null,\"messageParams\":null,\"propertyName\":\"login\"," +
                "\"propertyValue\":null,\"messageCode\":\"login.failed\",\"locale\":\"pl_PL\"," +
                "\"description\":\"Błąd logowania.\"}]}";

        String urlAddress = "https://login.nestbank.pl/rest/v1/auth/loginByPartialPassword";
        return getPage(urlAddress, new HashMap<>(), responseBody);
    }

    public static Page getPageValidBankAccounts(int userId) throws IOException {
        String responseBody = "{\"dashboardItems\":[{\"$objectType\":\"dashboardScreenFragment\"," +
                "\"data\":{\"$objectType\":\"mainProduct\",\"products\":[{\"$objectType\":\"accountProduct\"," +
                "\"id\":95294448,\"name\":\"Nest Konto\",\"availableAmount\":2000.00,\"currency\":\"PLN\"," +
                "\"balanceDate\":\"11.02.2019 12:10\",\"balance\":2000.00,\"waitingPayments\":[]," +
                "\"prevLoginBalance\":null,\"accountClassification\":\"CURRENT\"," +
                "\"lastOperations\":[{\"title\":\"Wypłata gotówki \\n" +
                "Nr karty ...5849 S1WA2390 WARSZAWA AL. JEROZOLIMSKIE 10 50,00 PLN\",\"amount\":50,\"side\":\"DEBIT\"}," +
                "{\"title\":\"Transakcja bezgotówkowa \\nNr karty ...5849 Krakow PayU*Allegro 34,33PLN\"," +
                "\"amount\":34.33,\"side\":\"DEBIT\"},{\"title\":\"Zasilenie konta\",\"amount\":1000," +
                "\"side\":\"CREDIT\"}]}]}},{\"$objectType\":\"dashboardScreenFragment\"," +
                "\"data\":{\"$objectType\":\"messagesAndWarningProduct\",\"rejectedOperationSummaries\":[]," +
                "\"forApprovalOperationSummaries\":[],\"totalRejectedOperationCount\":0," +
                "\"totalForApprovalOperationCount\":0,\"applicationsAndDispositionCounters\":{\"submittedCnt\":0," +
                "\"savedCnt\":0,\"toSignCnt\":0},\"creditCards\":[],\"totalCreditCardsDebit\":0,\"loans\":[]," +
                "\"totalLoanDebit\":0,\"futureOrdersWithFailedBlockadePerAccount\":[]," +
                "\"totalFutureOrdersWithFailedBlockadePerAccount\":0}},{\"$objectType\":\"dashboardScreenFragment\"," +
                "\"data\":{\"$objectType\":\"summaryProduct\",\"sumAmountsFromAccountsPerCurrencyLabel\":\"Konta:\"," +
                "\"sumCardLimitsPerCurrencyLabel\":\"Dostępny limit na kartach kredytowych:\"," +
                "\"sumSavingsAmountPerCurrencyLabel\":\"Moje oszczędności:\"," +
                "\"sumLoansAmountPerCurrencyLabel\":\"Moje kredyty do spłaty:\"," +
                "\"sumAmountsFromAccountsPerCurrency\":[{\"currency\":\"PLN\",\"amountSum\":2000.00,\"count\":1}]," +
                "\"sumCardLimitsPerCurrency\":[],\"sumSavingsAmountPerCurrency\":[{\"currency\":\"PLN\"," +
                "\"amountSum\":2000.0,\"count\":1}],\"sumLoansAmountPerCurrency\":[]}}],\"accounts\":[{\"id\":95274939," +
                "\"nrb\":\"91182010452056105003930001\",\"name\":\"Nest Konto\",\"openingBalance\":2000.00," +
                "\"openingBalanceDate\":\"2019-02-11 00:00:00\",\"balance\":2000.00," +
                "\"balanceDate\":\"2019-02-12 12:13:17\",\"availableFunds\":2000.00,\"currency\":\"PLN\",\"version\":20," +
                "\"status\":\"ACTIVE\",\"customerType\":\"PRIVATE\",\"unchargeable\":false,\"purposefulAccount\":false," +
                "\"accountClassification\":\"CURRENT\",\"ownTransfersOnly\":false,\"accountType\":\"RDM2_ZM_PL_SB\"," +
                "\"accountOpeningDate\":\"2019-01-15 00:00:00\",\"balanceDiff\":null,\"canCreatePocket\":true," +
                "\"prevLoginBalance\":null,\"vatAccountNrb\":null,\"default\":true,\"savingAccount\":false," +
                "\"vat\":false,\"ekid\":false},{\"id\":96194849,\"nrb\":\"65176010452078104003920002\"," +
                "\"name\":\"Nest Oszczędności\",\"openingBalance\":2000.0,\"openingBalanceDate\":\"2019-02-12 00:00:00\"," +
                "\"balance\":2000.0,\"balanceDate\":\"2019-02-13 12:14:17\",\"availableFunds\":2000.0,\"currency\":\"PLN\"," +
                "\"version\":11,\"status\":\"ACTIVE\",\"customerType\":\"PRIVATE\",\"unchargeable\":false," +
                "\"purposefulAccount\":false,\"accountClassification\":\"CURRENT\",\"ownTransfersOnly\":false," +
                "\"accountType\":\"RDS2_ZM_PL_SB\",\"accountOpeningDate\":\"2019-01-15 00:00:00\",\"balanceDiff\":null," +
                "\"canCreatePocket\":true,\"prevLoginBalance\":null,\"vatAccountNrb\":null,\"default\":false," +
                "\"savingAccount\":true,\"vat\":false,\"ekid\":false}],\"savingsAccounts\":[{\"id\":94273948," +
                "\"nrb\":\"65176010452078104003920002\",\"name\":\"Nest Oszczędności\",\"openingBalance\":2000.0," +
                "\"openingBalanceDate\":null,\"balance\":2000.0,\"balanceDate\":null,\"availableFunds\":2000.0," +
                "\"currency\":\"PLN\",\"version\":11,\"status\":\"ACTIVE\",\"customerType\":\"PRIVATE\"," +
                "\"unchargeable\":false,\"purposefulAccount\":false,\"accountClassification\":\"CURRENT\"," +
                "\"ownTransfersOnly\":false,\"accountType\":\"RDS2_ZM_PL_SB\",\"accountOpeningDate\":\"2019-01-14 00:00:00\"," +
                "\"capitalizationPeriodType\":\"MONTH\",\"capitalizationPeriodValue\":1,\"interestRate\":2.25," +
                "\"default\":false,\"savingAccount\":true}],\"deposits\":[],\"loans\":{\"pageCount\":0,\"count\":0," +
                "\"list\":[]},\"cards\":{\"pageCount\":1,\"count\":1,\"list\":[{\"id\":95284970,\"name\":\"Karta do Nest Konta\"," +
                "\"number\":\"4823 **** **** 5759\",\"nrb\":\"91182010452056105003930001\",\"availableFunds\":2000.00," +
                "\"type\":\"DEBIT\",\"status\":\"ACTIVE\",\"ownerName\":\"ADAM NOWAK\",\"currency\":\"PLN\"," +
                "\"expirationDate\":\"2022-01-30 00:00:00\",\"reservationDate\":null," +
                "\"cardAccessibilityArray\":{\"visibility\":true,\"details\":true,\"history\":true,\"cancel\":true," +
                "\"externalTransfer\":false,\"ownTransfer\":false,\"payoff\":false,\"changePin\":true,\"activate\":false," +
                "\"orderNew\":false,\"changeLimits\":true},\"defaultAction\":\"changePin\"}]}}";

        String urlAddress = "https://login.nestbank.pl/rest/v1/context/" + userId + "/dashboard/www/config";
        return getPage(urlAddress, new HashMap<>(), responseBody);
    }

    public static Page getPageBadUrl() throws IOException { // http 404
        String urlAddress = "https://bad.url.com";
        return getPage(urlAddress, new HashMap<>(), "");
    }

    public static Page getPageBadHttpMethod() throws IOException { // http 405
        String urlAddress = "https://bad.http.method.com";
        return getPage(urlAddress, new HashMap<>(), "");
    }

    public static Page getPageBadContentType() throws IOException { // http 415
        String urlAddress = "https://bad.content.type.com";
        return getPage(urlAddress, new HashMap<>(), "");
    }

    public static Page getPageBadReferer() throws IOException { // ignored by server, it's working normal way
        String urlAddress = "https://bad.referer.com";
        return getPage(urlAddress, new HashMap<>(), "");
    }

    public static Page getPageBadSessionToken() throws IOException { // http 401
        String urlAddress = "https://login.nestbank.pl/rest/v1/context/" + /*userId + */"/dashboard/www/config";
        String responseBody = "{\"level\":\"ERROR\",\"problems\":[{\"level\":\"ERROR\",\"objectId\":null," +
                "\"objectIdsList\":[],\"objectName\":null,\"messageParams\":null,\"propertyName\":null," +
                "\"propertyValue\":null,\"messageCode\":\"could not extract session token\",\"locale\":null," +
                "\"description\":null}]}";
        return getPage(urlAddress, new HashMap<>(), responseBody);
    }

    public static Page getPageBadAcceptHeader() throws IOException { // ignored by server, it's working normal way
        String urlAddress = "https://bad.accept.com";
        return getPage(urlAddress, new HashMap<>(), "");
    }

    public static Page getPageBadAcceptEncoding() throws IOException { // ignored by server, it's working normal way
        String urlAddress = "https://bad.accept.encoding.com";
        return getPage(urlAddress, new HashMap<>(), "");
    }

    public static Page getPageBadAcceptLanguage() throws IOException { // ignored by server, it's working normal way
        String urlAddress = "https://bad.accept.language.com";
        return getPage(urlAddress, new HashMap<>(), "");
    }

    private static Page getPage(String urlAddress, Map<String, String> responseHeaders, String responseBody)
            throws IOException {
        return new PageStub(urlAddress, responseHeaders, responseBody);
    }
}
