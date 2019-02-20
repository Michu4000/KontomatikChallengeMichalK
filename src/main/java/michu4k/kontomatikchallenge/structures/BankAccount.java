package michu4k.kontomatikchallenge.structures;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;

public class BankAccount {
    public String accountName;
    public int[] accountNumber;
    public BigDecimal accountBalance;
    public String accountCurrency;

    public boolean isInCredit() {
        int compareResult = accountBalance.compareTo(new BigDecimal("0"));
        return (compareResult > 0);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(accountName, accountBalance, accountCurrency);
        result = 31 * result + Arrays.hashCode(accountNumber);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof BankAccount))
            return false;

        BankAccount bankAccountObj = (BankAccount) obj;
        boolean isNameEqual = bankAccountObj.accountName.equals(this.accountName);
        boolean isNumberEqual = Arrays.equals(bankAccountObj.accountNumber, this.accountNumber);
        boolean isBalanceEqual = bankAccountObj.accountBalance.equals(this.accountBalance);
        boolean isCurrencyEqual = bankAccountObj.accountCurrency.equals(this.accountCurrency);
        return (isNameEqual && isNumberEqual && isBalanceEqual && isCurrencyEqual);
    }
}