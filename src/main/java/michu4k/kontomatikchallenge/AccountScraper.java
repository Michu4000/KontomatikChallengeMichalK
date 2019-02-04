package michu4k.kontomatikchallenge;

import java.util.List;

public interface AccountScraper {
    void scrapeAccounts();

    List<String> getAccountsNames();

    List<String> getAccountsNumbers();

    List<String> getAccountsBalances();

    List<String> getAccountsCurrencies();
}
