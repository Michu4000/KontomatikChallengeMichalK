package michu4k.kontomatikchallenge.datascrape;

import michu4k.kontomatikchallenge.datastructures.BankAccountData;
import michu4k.kontomatikchallenge.datastructures.BankSession;
import michu4k.kontomatikchallenge.exceptions.BankConnectionException;

import java.net.MalformedURLException;
import java.util.List;

public interface BankAccountScraper {
    List<BankAccountData> scrapeBankAccounts(BankSession bankSession)
            throws BankConnectionException, MalformedURLException;
}
