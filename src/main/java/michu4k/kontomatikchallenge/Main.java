// Michał Krysztofik
// Nest Bank Account Checker
//
// Notice:
// 1. Works only with partial password login method
// 2. User must know his avatar id
// 3. DEBUG_MODE sets proxy for testing with Burp
//
package michu4k.kontomatikchallenge;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.ProxyConfig;

import java.util.List;

public class Main {
    private final static boolean DEBUG_MODE = false;

    private static String login;
    private static String password ;
    private static String avatarId;
    private static WebClient webClient;
    private static String sessionToken;
    private static String userId;
    private static List<String> accountsNames;
    private static List<String> accountsNumbers;
    private static List<String> accountsBalances;
    private static List<String> accountsCurrencies;

    public static void main(String[] args) {
        checkArguments(args);
        if(!DEBUG_MODE) {
            createAndSetWebClient();
        }
        else {
            createAndSetWebClientWithProxy();
        }
        logIn();
        scrapeAccountsData();
        UserInterface.printAccountsBalance(accountsNames, accountsNumbers, accountsBalances, accountsCurrencies);
    }

    private static void checkArguments(String[] args) {
        switch (args.length) {
            case 0:
                // ask for login, password, avatarId
                login = UserInterface.askForLogin();
                password = UserInterface.askForPassword();
                avatarId = UserInterface.askForAvatar();
                break;

            case 1:
                // ask for password and avatarId
                login = args[0];
                password = UserInterface.askForPassword();
                avatarId = UserInterface.askForAvatar();
                break;

            case 2:
                // ask only for avatarId
                login = args[0];
                password = args[1];
                avatarId = UserInterface.askForAvatar();
                break;

            case 3:
                // don't ask - everything is in commandline arguments
                login = args[0];
                password = args[1];
                avatarId = args[2];
                break;

            default:
                UserInterface.printArgumentsError();
                System.exit(2); // terminate program with error status
                break;
        }

        // check for blank arguments/inputs
        if(login.length() == 0 || password.length() == 0 || avatarId.length() == 0) {
            UserInterface.printArgumentsError();
            System.exit(2);
        }
    }

    private static void createAndSetWebClient() {
        webClient = new WebClient();
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
    }

    private static void createAndSetWebClientWithProxy() {
        createAndSetWebClient();
        final ProxyConfig proxyConfig = new ProxyConfig("127.0.0.1", 8080);
        webClient.getOptions().setProxyConfig(proxyConfig);
        webClient.getOptions().setUseInsecureSSL(true);
    }

    private static void logIn() {
        final BankAuthenticator bankAuthenticator =
                new PartialPasswordBankAuthenticator(login, password, avatarId, webClient);
        bankAuthenticator.logIntoAccount();
        sessionToken = bankAuthenticator.getSessionToken();
        userId = bankAuthenticator.getUserId();
    }

    private static void scrapeAccountsData() {
        final AccountScraper accountScraper = new SimpleAccountScraper(sessionToken, userId, webClient);
        accountScraper.scrapeAccounts();
        accountsNames = accountScraper.getAccountsNames();
        accountsNumbers = accountScraper.getAccountsNumbers();
        accountsBalances = accountScraper.getAccountsBalances();
        accountsCurrencies = accountScraper.getAccountsCurrencies();
    }
}