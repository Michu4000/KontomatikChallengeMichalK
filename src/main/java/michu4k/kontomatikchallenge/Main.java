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
import michu4k.kontomatikchallenge.datascrape.BankAccountScraper;
import michu4k.kontomatikchallenge.datascrape.NestBankAccountScraper;
import michu4k.kontomatikchallenge.datastructures.BankAccount;
import michu4k.kontomatikchallenge.datastructures.BankSession;
import michu4k.kontomatikchallenge.datastructures.UserCredentials;
import michu4k.kontomatikchallenge.exceptions.BadArgumentsException;
import michu4k.kontomatikchallenge.exceptions.BadCredentialsException;
import michu4k.kontomatikchallenge.exceptions.BadLoginMethodException;
import michu4k.kontomatikchallenge.userinterface.ErrorsHandler;
import michu4k.kontomatikchallenge.userinterface.UserInterface;
import michu4k.kontomatikchallenge.utils.WebClientFactory;

import com.gargoylesoftware.htmlunit.WebClient;

import java.io.IOException;
import java.util.List;

//TODO ACCEPTANCE TESTs with different scenarios (successful login, bad login, bad password) using real bank system

public class Main {
    private final static boolean DEBUG_MODE = false;

    private static UserCredentials userCredentials;
    private static WebClient webClient;
    private static BankSession bankSession;
    private static List<BankAccount> bankAccounts;

    public static void main(String[] args) {
        enterCredentials(args);
        setupConnection();
        signIn();
        importBankAccounts();
        printBankAccounts();
    }

    private static void enterCredentials(String[] args) {
        try {
            userCredentials = UserInterface.findOutUserCredentials(args);
        } catch (BadArgumentsException badArgumentsException) {
            ErrorsHandler.handleException(badArgumentsException, DEBUG_MODE);
        }
    }

    private static void setupConnection() {
        if (!DEBUG_MODE)
            webClient = WebClientFactory.getWebClient();
        else
            webClient = WebClientFactory.getWebClientWithProxy();
    }

    private static void signIn() {
        BankAuthenticator bankAuthenticator = new NestBankMaskedPasswordAuthenticator(webClient);
        try {
            bankSession = bankAuthenticator.logIntoBankAccount(userCredentials);
        } catch (BadCredentialsException | BadLoginMethodException | IOException exception) {
            ErrorsHandler.handleException(exception, DEBUG_MODE);
        }
    }

    private static void importBankAccounts() {
        BankAccountScraper bankAccountScraper = new NestBankAccountScraper(webClient);
        try {
            bankAccounts = bankAccountScraper.scrapeBankAccounts(bankSession);
        } catch (IOException ioException) {
            ErrorsHandler.handleException(ioException, DEBUG_MODE);
        }
    }

    private static void printBankAccounts() {
        UserInterface.printBankAccounts(bankAccounts);
    }
}