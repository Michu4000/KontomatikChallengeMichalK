package michu4k.kontomatikchallenge;

import java.util.List;

public interface AccountChecker {
    void checkAccounts();

    List<String> getAccountsNames();

    List<String> getAccountsNumbers();

    List<String> getAccountsBalances();

    List<String> getAccountsCurrencies();
}
