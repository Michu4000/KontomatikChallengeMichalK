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

public class NestChecker {
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
        checkBank();
        Communicator.printAccountsBalance(accountsNames, accountsNumbers, accountsBalances, accountsCurrencies);
    }

    private static void checkArguments(String[] args) {
        switch (args.length) {
            case 0:
                // ask for login, password, avatarId
                login = Communicator.askForLogin();
                password = Communicator.askForPassword();
                avatarId = Communicator.askForAvatar();
                break;

            case 1:
                // ask for password and avatarId
                login = args[0];
                password = Communicator.askForPassword();
                avatarId = Communicator.askForAvatar();
                break;

            case 2:
                // ask only for avatarId
                login = args[0];
                password = args[1];
                avatarId = Communicator.askForAvatar();
                break;

            case 3:
                // don't ask - everything is in commandline arguments
                login = args[0];
                password = args[1];
                avatarId = args[2];
                break;

            default:
                Communicator.printArgumentsError();
                System.exit(2); // terminate program with error status
                break;
        }

        // check for blank arguments/inputs
        if(login.length() == 0 || password.length() == 0 || avatarId.length() == 0) {
            Communicator.printArgumentsError();
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
        final AccountLogger accountLogger =
                new PartialPasswordAccountLogger(login, password, avatarId, webClient);
        accountLogger.logIntoAccount();
        sessionToken = accountLogger.getSessionToken();
        userId = accountLogger.getUserId();
    }

    private static void checkBank() {
        final AccountChecker accountChecker = new SimpleAccountChecker(sessionToken, userId, webClient);
        accountChecker.checkAccounts();
        accountsNames = accountChecker.getAccountsNames();
        accountsNumbers = accountChecker.getAccountsNumbers();
        accountsBalances = accountChecker.getAccountsBalances();
        accountsCurrencies = accountChecker.getAccountsCurrencies();
    }
}