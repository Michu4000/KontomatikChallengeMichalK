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

import michu4k.kontomatikchallenge.bankauthentication.BankAuthenticator;
import michu4k.kontomatikchallenge.bankauthentication.PartialPasswordBankAuthenticator;
import michu4k.kontomatikchallenge.datascrape.BankAccountScraper;
import michu4k.kontomatikchallenge.datascrape.SimpleBankAccountScraper;
import michu4k.kontomatikchallenge.datastructures.BankAccountData;
import michu4k.kontomatikchallenge.datastructures.BankSession;
import michu4k.kontomatikchallenge.datastructures.UserCredentials;
import michu4k.kontomatikchallenge.userinterface.UserInterface;
import michu4k.kontomatikchallenge.utils.WebClientFactory;

import java.util.List;

public class Main {
    private final static boolean DEBUG_MODE = false;

    private static UserCredentials userCredentials;
    private static WebClient webClient;
    private static BankAuthenticator bankAuthenticator;
    private static BankSession bankSession;
    private static BankAccountScraper bankAccountScraper;
    private static List<BankAccountData> bankAccountsData;

    public static void main(String[] args) {
        userCredentials = UserInterface.findOutUserCredentials(args);

        if (!DEBUG_MODE) {
            webClient = WebClientFactory.getWebClient();
        }
        else {
            webClient = WebClientFactory.getWebClientWithProxy();
        }

        bankAuthenticator = new PartialPasswordBankAuthenticator(userCredentials, webClient);
        bankSession = bankAuthenticator.logIntoAccount();

        bankAccountScraper = new SimpleBankAccountScraper(bankSession, webClient);
        bankAccountsData = bankAccountScraper.scrapeAccounts();

        UserInterface.printAccountsBalance(bankAccountsData);
    }
}