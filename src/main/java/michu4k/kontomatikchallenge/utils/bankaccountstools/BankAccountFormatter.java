package michu4k.kontomatikchallenge.utils.bankaccountstools;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;

class BankAccountFormatter {
    static String formatAccountNumber(int[] accountNumber) {
        // iban format: XX XXXX XXXX XXXX XXXX XXXX XXXX
        String unformattedAccountNumber = Arrays.toString(accountNumber).replaceAll("\\D", "");
        StringBuilder formattedAccountNumberBuilder = getGroupedFirst2Digits(unformattedAccountNumber);
        return getGroupedEvery4Digits(unformattedAccountNumber, formattedAccountNumberBuilder).toString();
    }

    static String formatAccountBalance(BigDecimal accountBalance) {
        DecimalFormat balanceFormat = new DecimalFormat("#.00");
        return balanceFormat.format(accountBalance);
    }

    private static StringBuilder getGroupedFirst2Digits(String unformattedAccountNumber) {
        StringBuilder formattedAccountNumberBuilder = new StringBuilder();
        formattedAccountNumberBuilder.append(unformattedAccountNumber, 0, 2);
        return formattedAccountNumberBuilder;
    }

    private static StringBuilder getGroupedEvery4Digits(String unformattedAccountNumber, StringBuilder formattedAccountNumberBuilder) {
        for (int i = 2; i <= 22; i += 4) {
            formattedAccountNumberBuilder
                .append(" ")
                .append(unformattedAccountNumber, i, i + 4);
        }
        return formattedAccountNumberBuilder;
    }
}