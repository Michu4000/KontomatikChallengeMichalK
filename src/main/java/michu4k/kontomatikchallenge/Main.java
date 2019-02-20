// Michał Krysztofik
// Nest Bank Account Checker
//
// Notice:
// 1. Works only with masked password login method
// (otherwise BadLoginMethodException is thrown and internal application error is printed)
// 2. User must know his avatar id
// 3. DEBUG_MODE sets proxy for testing with Burp and prints stacktrace if exception is thrown
//
package michu4k.kontomatikchallenge;

import michu4k.kontomatikchallenge.bankauthentication.BankAuthenticator;
import michu4k.kontomatikchallenge.bankauthentication.NestBankMaskedPasswordAuthenticator;
import michu4k.kontomatikchallenge.scrapers.BankAccountScraper;
import michu4k.kontomatikchallenge.scrapers.NestBankAccountScraper;
import michu4k.kontomatikchallenge.structures.BankAccount;
import michu4k.kontomatikchallenge.structures.BankSession;
import michu4k.kontomatikchallenge.structures.UserCredentials;
import michu4k.kontomatikchallenge.exceptions.BadArgumentsException;
import michu4k.kontomatikchallenge.userinterface.ErrorsHandler;
import michu4k.kontomatikchallenge.userinterface.UserInterface;
import michu4k.kontomatikchallenge.utils.factories.WebClientFactory;

import com.gargoylesoftware.htmlunit.WebClient;

import java.util.Collections;
import java.util.List;

public class Main {
    private final static boolean DEBUG_MODE = false;

    public static void main(String[] args) {
        UserCredentials userCredentials = readUserCredentials(args);
        WebClient webClient = setupConnection();
        List<BankAccount> bankAccounts = importBankAccounts(userCredentials, webClient);
        printBankAccounts(bankAccounts);
    }

    private static UserCredentials readUserCredentials(String[] args) {
        try {
            return UserInterface.findOutUserCredentials(args);
        } catch (BadArgumentsException badArgumentsException) {
            ErrorsHandler.handleException(badArgumentsException, DEBUG_MODE);
        }
        return new UserCredentials();
    }

    private static WebClient setupConnection() {
        if (!DEBUG_MODE)
            return WebClientFactory.getWebClient();
        else
            return WebClientFactory.getWebClientWithProxy();
    }

    private static List<BankAccount> importBankAccounts(UserCredentials userCredentials, WebClient webClient) {
        BankAuthenticator bankAuthenticator = new NestBankMaskedPasswordAuthenticator(webClient);
        BankAccountScraper bankAccountScraper = new NestBankAccountScraper(webClient);
        try {
            BankSession bankSession = bankAuthenticator.logIntoBankAccount(userCredentials);
            return bankAccountScraper.scrapeBankAccounts(bankSession);
        } catch (Exception exception) {
            ErrorsHandler.handleException(exception, DEBUG_MODE);
        }
        return Collections.emptyList();
    }

    private static void printBankAccounts(List<BankAccount> bankAccounts) {
        UserInterface.printBankAccounts(bankAccounts);
    }
}