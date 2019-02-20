package michu4k.kontomatikchallenge.utils.bankaccountstools;

import michu4k.kontomatikchallenge.structures.BankAccount;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class JsonBankAccountsExtractor {
    public static List<BankAccount> extractBankAccountsFromJsonArray(JsonArray jsonBankAccounts) {
        List<BankAccount> bankAccountsGroup = new ArrayList<>();
        for (JsonValue rawJsonBankAccount : jsonBankAccounts) {
            JsonObject jsonBankAccount = rawJsonBankAccount.asJsonObject();

            BankAccount bankAccount = new BankAccount();
            bankAccount.accountName = extractName(jsonBankAccount);
            bankAccount.accountNumber = extractNumber(jsonBankAccount);
            bankAccount.accountBalance = extractBalance(jsonBankAccount);
            bankAccount.accountCurrency = extractCurrency(jsonBankAccount);
            bankAccountsGroup.add(bankAccount);
        }
        return bankAccountsGroup;
    }

    private static String extractName(JsonObject jsonBankAccount) {
        return jsonBankAccount.getString("name");
    }

    private static int[] extractNumber(JsonObject jsonBankAccount) {
        Stream<String> bankAccountNumberStream = Arrays.stream(jsonBankAccount.getString("nrb").split(""));
        return bankAccountNumberStream.mapToInt(Integer::parseInt).toArray();
    }

    private static BigDecimal extractBalance(JsonObject jsonBankAccount) {
        return jsonBankAccount.getJsonNumber("balance").bigDecimalValue();
    }

    private static String extractCurrency(JsonObject jsonBankAccount) {
        return jsonBankAccount.getString("currency");
    }
}