package michu4k.kontomatikchallenge.datastructures;

import java.math.BigDecimal;

public class BankAccountData {
    private String accountName;
    private int[] accountNumber;
    private BigDecimal accountBalance;
    private String accountCurrency;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public int[] getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int[] accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(BigDecimal accountBalance) {
        this.accountBalance = accountBalance;
    }

    public String getAccountCurrency() {
        return accountCurrency;
    }

    public void setAccountCurrency(String accountCurrency) {
        this.accountCurrency = accountCurrency;
    }
}
