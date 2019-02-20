package michu4k.kontomatikchallenge.scrapers;

import michu4k.kontomatikchallenge.structures.BankAccount;
import michu4k.kontomatikchallenge.structures.BankSession;

import java.io.IOException;
import java.util.List;

public interface BankAccountScraper {
    List<BankAccount> scrapeBankAccounts(BankSession bankSession) throws IOException;
}