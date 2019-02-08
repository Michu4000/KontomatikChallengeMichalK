package michu4k.kontomatikchallenge.datascrape;

import michu4k.kontomatikchallenge.datastructures.BankAccount;
import michu4k.kontomatikchallenge.datastructures.BankSession;

import java.io.IOException;
import java.util.List;

public interface BankAccountScraper {
    List<BankAccount> scrapeBankAccounts(BankSession bankSession) throws IOException;

}
