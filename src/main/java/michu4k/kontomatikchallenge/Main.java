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
import michu4k.kontomatikchallenge.userinterface.ErrorsPrinter;
import michu4k.kontomatikchallenge.userinterface.UserInterface;
import michu4k.kontomatikchallenge.utils.WebClientFactory;

import com.gargoylesoftware.htmlunit.WebClient;

import java.io.IOException;
import java.net.MalformedURLException;
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
        } catch (BadArgumentsException | NumberFormatException badArgumentsException) {
            if (DEBUG_MODE)
                badArgumentsException.printStackTrace();
            ErrorsPrinter.printArgumentsError();
            System.exit(2);
        }

        getWebClient();

        try {
            signIn();
            importBankAccounts();
        } catch (BadCredentialsException badCredentialsException) {
            if (DEBUG_MODE)
                badCredentialsException.printStackTrace();
            ErrorsPrinter.printBadCredentialsError();
            System.exit(2);
        } catch (MalformedURLException malformedURLException) {
            if (DEBUG_MODE)
                malformedURLException.printStackTrace();
            ErrorsPrinter.printInternalApplicationError();
            System.exit(3);
        }catch (IOException | NumberFormatException ioException) {
            if (DEBUG_MODE)
                ioException.printStackTrace();
            ErrorsPrinter.printConnectionError();
            System.exit(1);
        }

        UserInterface.printBankAccounts(bankAccounts);
    }

    private static void enterCredentials(String[] args) {
        userCredentials = UserInterface.findOutUserCredentials(args);
    }

    private static void getWebClient() {
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