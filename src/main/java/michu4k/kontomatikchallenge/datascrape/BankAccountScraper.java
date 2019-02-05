package michu4k.kontomatikchallenge.datascrape;

import michu4k.kontomatikchallenge.datastructures.BankAccountData;

import java.util.List;

public interface BankAccountScraper {
    List<BankAccountData> scrapeAccounts();
}
