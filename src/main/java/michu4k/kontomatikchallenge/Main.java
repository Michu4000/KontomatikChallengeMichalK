// Michał Krysztofik
// Nest Bank Account Checker
//
// Notice:
// 1. Works only with masked password login method
// 2. User must know his avatar id
// 3. DEBUG_MODE sets proxy for testing with Burp
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
import michu4k.kontomatikchallenge.userinterface.ErrorsHandler;
import michu4k.kontomatikchallenge.userinterface.UserInterface;
import michu4k.kontomatikchallenge.utils.WebClientFactory;

import com.gargoylesoftware.htmlunit.WebClient;

import java.io.IOException;
import java.util.List;

//TODO WRITE TESTs!!! For classes with logic
//TODO with TestNG

public class Main {
    private final static boolean DEBUG_MODE = false;

    private static UserCredentials userCredentials;
    private static WebClient webClient;
    private static BankSession bankSession;
    private static List<BankAccount> bankAccounts;

    public static void main(String[] args) {
        try {
            enterCredentials(args);
        } catch (BadArgumentsException badArgumentsException) {
            ErrorsHandler.handleException(badArgumentsException, DEBUG_MODE);
        }

        setupConnection();

        try {
            signIn();
            importBankAccounts();
        } catch (BadCredentialsException | IOException exception) {
            ErrorsHandler.handleException(exception, DEBUG_MODE);
        }

        UserInterface.printBankAccounts(bankAccounts);
    }

    private static void enterCredentials(String[] args) {
        userCredentials = UserInterface.findOutUserCredentials(args);
    }

    private static void setupConnection() {
        if (!DEBUG_MODE)
            webClient = WebClientFactory.getWebClient();
        else
            webClient = WebClientFactory.getWebClientWithProxy();
    }

    private static void signIn() throws IOException {
        BankAuthenticator bankAuthenticator = new NestBankMaskedPasswordAuthenticator(webClient);
        bankSession = bankAuthenticator.logIntoBankAccount(userCredentials);
    }

    private static void importBankAccounts() throws IOException {
        BankAccountScraper bankAccountScraper = new NestBankAccountScraper(webClient);
        bankAccounts = bankAccountScraper.scrapeBankAccounts(bankSession);
    }
}