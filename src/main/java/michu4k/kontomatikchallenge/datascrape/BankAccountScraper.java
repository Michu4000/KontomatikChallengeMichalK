package michu4k.kontomatikchallenge.datascrape;

import michu4k.kontomatikchallenge.datastructures.BankAccountData;
import michu4k.kontomatikchallenge.datastructures.BankSession;
import michu4k.kontomatikchallenge.exceptions.BankConnectionException;

import java.util.List;

public interface BankAccountScraper {
    List<BankAccountData> scrapeAccounts(BankSession bankSession) throws BankConnectionException;
}
