package michu4k.kontomatikchallenge.datastructures;

import java.math.BigDecimal;
import java.util.Arrays;

public class BankAccount {
    public String accountName;
    public int[] accountNumber;
    public BigDecimal accountBalance;
    public String accountCurrency;

    public boolean isInCredit() {
        int compareResult = accountBalance.compareTo(new BigDecimal("0"));
        if (compareResult > 0)
            return true;
        else
            return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof BankAccount))
            return false;
        boolean isNameEqual = ((BankAccount) obj).accountName.equals(this.accountName);
        boolean isNumberEqual = Arrays.equals(((BankAccount) obj).accountNumber, this.accountNumber);
        boolean isBalanceEqual = ((BankAccount) obj).accountBalance.equals(this.accountBalance);
        boolean isCurrencyEqual = ((BankAccount) obj).accountCurrency.equals(this.accountCurrency);
        if (isNameEqual && isNumberEqual && isBalanceEqual && isCurrencyEqual)
            return true;
        else
            return false;
    }
}