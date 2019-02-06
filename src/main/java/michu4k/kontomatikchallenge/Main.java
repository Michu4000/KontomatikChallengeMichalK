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
import michu4k.kontomatikchallenge.bankauthentication.NestBankPartialPasswordAuthenticator;
import michu4k.kontomatikchallenge.datascrape.BankAccountScraper;
import michu4k.kontomatikchallenge.datascrape.NestBankAccountScraper;
import michu4k.kontomatikchallenge.datastructures.BankAccountData;
import michu4k.kontomatikchallenge.datastructures.BankSession;
import michu4k.kontomatikchallenge.datastructures.UserCredentials;
import michu4k.kontomatikchallenge.exceptions.*;
import michu4k.kontomatikchallenge.userinterface.ErrorsPrinter;
import michu4k.kontomatikchallenge.userinterface.UserInterface;
import michu4k.kontomatikchallenge.utils.WebClientFactory;

import java.net.MalformedURLException;
import java.util.List;

public class Main {
    private final static boolean DEBUG_MODE = false;

    public static void main(String[] args) {
        UserCredentials userCredentials = fetchCredentialsFromUserInterface(args);

        WebClient webClient;
        if (!DEBUG_MODE) {
            webClient = WebClientFactory.getWebClient();
        }
        else {
            webClient = WebClientFactory.getWebClientWithProxy();
        }

        BankAuthenticator bankAuthenticator = new NestBankPartialPasswordAuthenticator(webClient);
        BankSession bankSession = fetchBankSessionFromBankAuthenticator(bankAuthenticator, userCredentials);

        BankAccountScraper bankAccountScraper = new NestBankAccountScraper(webClient);
        List<BankAccountData> bankAccountsData = fetchBankAccountsDataFromBankAccountScrapper(bankAccountScraper, bankSession);

        UserInterface.printBankAccountsBalance(bankAccountsData);
    }

    private static UserCredentials fetchCredentialsFromUserInterface(String[] args) {
        UserCredentials userCredentials = null;
        try {
            userCredentials = UserInterface.findOutUserCredentials(args);
        } catch (BadArgumentsException e) {
            if(DEBUG_MODE) {
                e.printStackTrace();
            }
            ErrorsPrinter.printArgumentsError();
            System.exit(2);
        }
        return userCredentials;
    }

    private static BankSession fetchBankSessionFromBankAuthenticator(BankAuthenticator bankAuthenticator,
                                                                     UserCredentials userCredentials) {
        BankSession bankSession = null;
        try {
            bankSession = bankAuthenticator.logIntoBankAccount(userCredentials);
        } catch (BankConnectionException e) {
            if(DEBUG_MODE) {
                e.printStackTrace();
            }
            ErrorsPrinter.printConnectionError();
            System.exit(1);
        } catch (BadLoginException e2) {
            if(DEBUG_MODE) {
                e2.printStackTrace();
            }
            ErrorsPrinter.printBadLoginError();
            System.exit(2);
        } catch (BadPasswordException e3) {
            if(DEBUG_MODE) {
                e3.printStackTrace();
            }
            ErrorsPrinter.printBadPasswordError();
            System.exit(2);
        } catch (BadCredentialsException e4) {
            if(DEBUG_MODE) {
                e4.printStackTrace();
            }
            ErrorsPrinter.printBadCredentialsError();
            System.exit(2);
        } catch (MalformedURLException e2) {
            if (DEBUG_MODE) {
                e2.printStackTrace();
            }
            ErrorsPrinter.printInternalApplictionError();
            System.exit(3);
        }
        return bankSession;
    }

    private static List<BankAccountData> fetchBankAccountsDataFromBankAccountScrapper(BankAccountScraper bankAccountScraper,
                                                                                      BankSession bankSession) {
        List<BankAccountData> bankAccountsData = null;
        try {
            bankAccountsData = bankAccountScraper.scrapeBankAccounts(bankSession);
        } catch (BankConnectionException e) {
            if (DEBUG_MODE) {
                e.printStackTrace();
            }
            ErrorsPrinter.printConnectionError();
            System.exit(1);
        } catch (MalformedURLException e2) {
            if (DEBUG_MODE) {
                e2.printStackTrace();
            }
            ErrorsPrinter.printInternalApplictionError();
            System.exit(3);
        }
        return bankAccountsData;
    }
}