package michu4k.kontomatikchallenge.utils;

import michu4k.kontomatikchallenge.datastructures.BankAccount;

import javax.json.JsonArray;
import javax.json.JsonValue;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class JsonBankAccountsExtractor {
    public static List<BankAccount> extractBankAccountsFromJsonArray(JsonArray jsonBankAccounts) {
        List<BankAccount> bankAccountsGroup = new ArrayList<>();
        for (JsonValue jsonBankAccount : jsonBankAccounts) {
            BankAccount bankAccount = new BankAccount();
            bankAccount.accountName = extractName(jsonBankAccount);
            bankAccount.accountNumber = extractNumber(jsonBankAccount);
            bankAccount.accountBalance = extractBalance(jsonBankAccount);
            bankAccount.accountCurrency = extractCurrency(jsonBankAccount);
            bankAccountsGroup.add(bankAccount);
        }
        return bankAccountsGroup;
    }

    private static String extractName(JsonValue jsonBankAccount) {
        return jsonBankAccount.asJsonObject().getString("name");
    }

    private static int[] extractNumber(JsonValue jsonBankAccount) {
        Stream<String> bankAccountNumberStream = Arrays.stream(jsonBankAccount.asJsonObject().getString("nrb").split(""));
        return bankAccountNumberStream.mapToInt(Integer::parseInt).toArray();
    }

    private static BigDecimal extractBalance(JsonValue jsonBankAccount) {
        return jsonBankAccount.asJsonObject().getJsonNumber("balance").bigDecimalValue();
    }

    private static String extractCurrency(JsonValue jsonBankAccount) {
        return jsonBankAccount.asJsonObject().getString("currency");
    }
}