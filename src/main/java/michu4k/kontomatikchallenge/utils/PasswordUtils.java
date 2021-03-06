package michu4k.kontomatikchallenge.utils;

import michu4k.kontomatikchallenge.exceptions.badcredentials.BadPasswordException;
import michu4k.kontomatikchallenge.structures.UserCredentials;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class PasswordUtils {
    public static int[] extractMaskedPasswordKeysIndexesFromResponse(String loginResponse) {
        JsonArray maskedPasswordKeysJsonArray = JsonUtils.parseStringToJsonArray(loginResponse, "passwordKeys");
        int[] maskedPasswordKeysIndexes = maskedPasswordKeysJsonArray
            .getValuesAs(JsonNumber.class)
            .stream()
            .mapToInt(JsonNumber::intValue)
            .toArray();
        return maskedPasswordKeysIndexes;
    }

    public static void checkPasswordLength(String password, int[] maskedPasswordKeysIndexes) {
        int minLength = maskedPasswordKeysIndexes[maskedPasswordKeysIndexes.length - 1];
        if (!isPasswordLongEnough(minLength, password.length()))
            throw new BadPasswordException("Password is too short");
    }

    public static String buildPasswordAndAvatarRequestBody(int[] maskedPasswordKeysIndexes, UserCredentials userCredentials) {
        JsonObjectBuilder masterBuilder = Json.createObjectBuilder();
        JsonObjectBuilder maskedPasswordBuilder = buildMaskedPassword(maskedPasswordKeysIndexes, userCredentials.password);
        masterBuilder.
            add("login", userCredentials.login)
            .add("maskedPassword", maskedPasswordBuilder)
            .add("avatarId", userCredentials.avatarId)
            .add("loginScopeType", "WWW");
        JsonObject jsonPasswordAndAvatarRequestBody = masterBuilder.build();
        return JsonUtils.writeJsonToString(jsonPasswordAndAvatarRequestBody);
    }

    public static JsonObjectBuilder buildMaskedPassword(int[] maskedPasswordKeysIndexes, String password) {
        JsonObjectBuilder maskedPasswordBuilder = Json.createObjectBuilder();
        for (int maskedPasswordKeyIdx : maskedPasswordKeysIndexes) {
            maskedPasswordBuilder.add(
                String.valueOf(maskedPasswordKeyIdx),
                password.substring(maskedPasswordKeyIdx - 1, maskedPasswordKeyIdx)
            );
        }
        return maskedPasswordBuilder;
    }

    private static boolean isPasswordLongEnough(int minLength, int passwordLength) {
        return passwordLength >= minLength;
    }
}