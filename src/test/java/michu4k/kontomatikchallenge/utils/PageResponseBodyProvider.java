package michu4k.kontomatikchallenge.utils;

class PageResponseBodyProvider {
    static final String BAD_LOGIN =
            "{\"level\":\"ERROR\",\"problems\":[{\"level\":\"ERROR\",\"objectId\":null,\"objectIdsList\":[]," +
            "\"objectName\":null,\"messageParams\":null,\"propertyName\":\"login\",\"description\":\"login.incorrect\"}]}";

    static final String VALID_LOGIN_BEGINNING =
            "{\"avatars\":[{\"id\":2,\"url\":\"/rest/resource?id=2\",\"name\":\"ptak\"}," +
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
            "{\"id\":7,\"url\":\"/rest/resource?id=7\",\"name\":\"ster\"}],";

    static final String VALID_LOGIN_METHOD_FULL_PASSWORD = "\"loginProcess\":\"FULL_PASSWORD\"}";

    static final String BAD_PASSWORD_AND_AVATAR =
            "{\"level\":\"ERROR\",\"problems\":[{\"level\":\"ERROR\",\"objectId\":null,\"objectIdsList\":[]," +
            "\"objectName\":null,\"messageParams\":null,\"propertyName\":\"login\",\"propertyValue\":null," +
            "\"messageCode\":\"login.failed\",\"locale\":\"pl_PL\",\"description\":\"Błąd logowania.\"}]}";

    static final String VALID_PASSWORD_AND_AVATAR_BEGINNING = "{\"userContexts\":[{\"id\":";

    static final String VALID_PASSWORD_AND_AVATAR_END =
            ",\"name\":\"ADAM NOWAK\",\"type\":\"PRIVATE\",\"customerPesel\":null,\"customerRegon\":null," +
            "\"customerNip\":null,\"address\":null,\"customerId\":96224831,\"selected\":true,\"cf\":false}," +
            "{\"id\":48705044,\"name\":\"ADAM NOWAK\",\"type\":\"CORPORATE\",\"customerPesel\":null," +
            "\"customerRegon\":null,\"customerNip\":null,\"address\":null,\"customerId\":128652005," +
            "\"selected\":false,\"cf\":false}],\"lastSuccessLoginDate\":\"2019-02-13 12:00:13\"," +
            "\"authorizationPasswordShouldBeSet\":false,\"authorizationPasswordEnabledFrom\":null," +
            "\"userShouldSetAuthorizationToken\":false,\"sessionTimeoutValue\":5}";

    static final String VALID_BANK_ACCOUNTS = "{\"dashboardItems\":[{\"$objectType\":\"dashboardScreenFragment\"," +
            "\"data\":{\"$objectType\":\"mainProduct\",\"products\":[{\"$objectType\":\"accountProduct\"," +
            "\"id\":66666666,\"name\":\"Nest Konto\",\"availableAmount\":2000.00,\"currency\":\"PLN\"," +
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
            "\"amountSum\":3000.00,\"count\":1}],\"sumLoansAmountPerCurrency\":[]}}],\"accounts\":[{\"id\":66666666," +
            "\"nrb\":\"66555544443333222211110000\",\"name\":\"Nest Konto\",\"openingBalance\":2000.00," +
            "\"openingBalanceDate\":\"2019-02-11 00:00:00\",\"balance\":2000.00," +
            "\"balanceDate\":\"2019-02-12 12:13:17\",\"availableFunds\":2000.00,\"currency\":\"PLN\",\"version\":20," +
            "\"status\":\"ACTIVE\",\"customerType\":\"PRIVATE\",\"unchargeable\":false,\"purposefulAccount\":false," +
            "\"accountClassification\":\"CURRENT\",\"ownTransfersOnly\":false,\"accountType\":\"RDM2_ZM_PL_SB\"," +
            "\"accountOpeningDate\":\"2019-01-15 00:00:00\",\"balanceDiff\":null,\"canCreatePocket\":true," +
            "\"prevLoginBalance\":null,\"vatAccountNrb\":null,\"default\":true,\"savingAccount\":false," +
            "\"vat\":false,\"ekid\":false},{\"id\":77777777,\"nrb\":\"00111122223333444455556666\"," +
            "\"name\":\"Nest Oszczędności\",\"openingBalance\":3000.00,\"openingBalanceDate\":\"2019-02-12 00:00:00\"," +
            "\"balance\":3000.00,\"balanceDate\":\"2019-02-13 12:14:17\",\"availableFunds\":3000.00,\"currency\":\"PLN\"," +
            "\"version\":11,\"status\":\"ACTIVE\",\"customerType\":\"PRIVATE\",\"unchargeable\":false," +
            "\"purposefulAccount\":false,\"accountClassification\":\"CURRENT\",\"ownTransfersOnly\":false," +
            "\"accountType\":\"RDS2_ZM_PL_SB\",\"accountOpeningDate\":\"2019-01-15 00:00:00\",\"balanceDiff\":null," +
            "\"canCreatePocket\":true,\"prevLoginBalance\":null,\"vatAccountNrb\":null,\"default\":false," +
            "\"savingAccount\":true,\"vat\":false,\"ekid\":false}],\"savingsAccounts\":[{\"id\":77777777," +
            "\"nrb\":\"00111122223333444455556666\",\"name\":\"Nest Oszczędności\",\"openingBalance\":3000.00," +
            "\"openingBalanceDate\":null,\"balance\":3000.00,\"balanceDate\":null,\"availableFunds\":3000.00," +
            "\"currency\":\"PLN\",\"version\":11,\"status\":\"ACTIVE\",\"customerType\":\"PRIVATE\"," +
            "\"unchargeable\":false,\"purposefulAccount\":false,\"accountClassification\":\"CURRENT\"," +
            "\"ownTransfersOnly\":false,\"accountType\":\"RDS2_ZM_PL_SB\",\"accountOpeningDate\":\"2019-01-14 00:00:00\"," +
            "\"capitalizationPeriodType\":\"MONTH\",\"capitalizationPeriodValue\":1,\"interestRate\":2.25," +
            "\"default\":false,\"savingAccount\":true}],\"deposits\":[],\"loans\":{\"pageCount\":0,\"count\":0," +
            "\"list\":[]},\"cards\":{\"pageCount\":1,\"count\":1,\"list\":[{\"id\":88888888,\"name\":\"Karta do Nest Konta\"," +
            "\"number\":\"4823 **** **** 5759\",\"nrb\":\"66555544443333222211110000\",\"availableFunds\":2000.00," +
            "\"type\":\"DEBIT\",\"status\":\"ACTIVE\",\"ownerName\":\"ADAM NOWAK\",\"currency\":\"PLN\"," +
            "\"expirationDate\":\"2022-01-30 00:00:00\",\"reservationDate\":null," +
            "\"cardAccessibilityArray\":{\"visibility\":true,\"details\":true,\"history\":true,\"cancel\":true," +
            "\"externalTransfer\":false,\"ownTransfer\":false,\"payoff\":false,\"changePin\":true,\"activate\":false," +
            "\"orderNew\":false,\"changeLimits\":true},\"defaultAction\":\"changePin\"}]}}";

    static final String BAD_SESSION_TOKEN = "{\"level\":\"ERROR\",\"problems\":[{\"level\":\"ERROR\",\"objectId\":null," +
            "\"objectIdsList\":[],\"objectName\":null,\"messageParams\":null,\"propertyName\":null," +
            "\"propertyValue\":null,\"messageCode\":\"could not extract session token\",\"locale\":null," +
            "\"description\":null}]}";
}
