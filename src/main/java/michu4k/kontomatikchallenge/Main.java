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
import michu4k.kontomatikchallenge.exceptions.BadCredentialsException;
import michu4k.kontomatikchallenge.exceptions.BadLoginException;
import michu4k.kontomatikchallenge.exceptions.BadPasswordException;
import michu4k.kontomatikchallenge.exceptions.BankConnectionException;
import michu4k.kontomatikchallenge.userinterface.ErrorsPrinter;
import michu4k.kontomatikchallenge.userinterface.UserInterface;
import michu4k.kontomatikchallenge.utils.WebClientFactory;

import java.util.List;

public class Main {
    private final static boolean DEBUG_MODE = false;

    public static void main(String[] args) {
        UserCredentials userCredentials;
        WebClient webClient;
        BankAuthenticator bankAuthenticator;
        BankSession bankSession = null;
        BankAccountScraper bankAccountScraper;
        List<BankAccountData> bankAccountsData = null;

        userCredentials = UserInterface.findOutUserCredentials(args);

        if (!DEBUG_MODE) {
            webClient = WebClientFactory.getWebClient();
        }
        else {
            webClient = WebClientFactory.getWebClientWithProxy();
        }

        bankAuthenticator = new PartialPasswordBankAuthenticator(webClient);
        try {
            bankSession = bankAuthenticator.logIntoAccount(userCredentials);
        } catch (BankConnectionException e) {
            e.printStackTrace();
            ErrorsPrinter.printConnectionError();
            System.exit(1);
        } catch (BadLoginException e2) {
            e2.printStackTrace();
            ErrorsPrinter.printBadLoginError();
            System.exit(2);
        } catch (BadPasswordException e3) {
            e3.printStackTrace();
            ErrorsPrinter.printBadPasswordError();
            System.exit(2);
        } catch (BadCredentialsException e4) {
            e4.printStackTrace();
            ErrorsPrinter.printBadCredentialsError();
            System.exit(2);
        }

        bankAccountScraper = new SimpleBankAccountScraper(webClient);
        try {
            bankAccountsData = bankAccountScraper.scrapeAccounts(bankSession);
        } catch (BankConnectionException e) {
            e.printStackTrace();
            ErrorsPrinter.printConnectionError();
            System.exit(1);
        }

        UserInterface.printAccountsBalance(bankAccountsData);
    }
}