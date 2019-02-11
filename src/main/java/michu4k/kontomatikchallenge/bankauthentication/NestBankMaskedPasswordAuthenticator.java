package michu4k.kontomatikchallenge.bankauthentication;

import michu4k.kontomatikchallenge.datastructures.BankSession;
import michu4k.kontomatikchallenge.datastructures.UserCredentials;
import michu4k.kontomatikchallenge.exceptions.BadCredentialsException;
import michu4k.kontomatikchallenge.exceptions.BadLoginNameException;
import michu4k.kontomatikchallenge.exceptions.BadLoginMethodException;
import michu4k.kontomatikchallenge.exceptions.BadPasswordException;
import michu4k.kontomatikchallenge.utils.WebRequestFactory;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import javax.json.JsonObject;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonReader;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObjectBuilder;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;

public class NestBankMaskedPasswordAuthenticator implements BankAuthenticator {
    private final static String LOGIN_SITE_URL = "https://login.nestbank.pl/rest/v1/auth/checkLogin";
    private final static String PASSWORD_AND_AVATAR_SITE_URL =
            "https://login.nestbank.pl/rest/v1/auth/loginByPartialPassword";

    private final WebClient webClient;
    private String loginResponse;
    private BankSession bankSession;

    public NestBankMaskedPasswordAuthenticator(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public BankSession logIntoBankAccount(UserCredentials userCredentials) throws IOException {
        try {
            enterLogin(userCredentials.login);
        } catch (FailingHttpStatusCodeException failingHttpStatusCodeException) {
            throw new BadLoginNameException(failingHttpStatusCodeException);
        } catch (JsonException | IllegalStateException jsonException) {
            throw new IOException(jsonException);
        }

        try {
            enterPasswordAndAvatar(userCredentials);
        } catch (FailingHttpStatusCodeException failingHttpStatusCodeException) {
            throw new BadCredentialsException(failingHttpStatusCodeException);
        } catch (JsonException | IllegalStateException | ClassCastException jsonException) {
            throw new IOException(jsonException);
        }

        return bankSession;
    }

    private void enterLogin(String login) throws IOException {
        URL loginSiteUrl = new URL(LOGIN_SITE_URL);

        JsonObject loginJson = Json.createObjectBuilder().add("login", login).build();
        String loginJsonString = writeJsonToString(loginJson);

        WebRequest loginSendRequest = WebRequestFactory.createRequestPost(loginSiteUrl, loginJsonString);
        Page passwordPage = webClient.getPage(loginSendRequest);
        loginResponse = passwordPage.getWebResponse().getContentAsString();
    }

    private void enterPasswordAndAvatar(UserCredentials userCredentials) throws IOException {
        int[] maskedPasswordKeysIndexes = extractMaskedPasswordKeysIndexesFromResponse();

        checkPasswordLength(userCredentials.password, maskedPasswordKeysIndexes[maskedPasswordKeysIndexes.length-1]);

        String passwordAndAvatarSendRequestBody = buildMaskedPassword(maskedPasswordKeysIndexes, userCredentials);
        URL passwordAndAvatarUrl = new URL(PASSWORD_AND_AVATAR_SITE_URL);

        WebRequest passwordAndAvatarSendRequest = WebRequestFactory.createRequestPost(passwordAndAvatarUrl,
                passwordAndAvatarSendRequestBody);

        Page afterLoginPage = webClient.getPage(passwordAndAvatarSendRequest);

        String passwordAndAvatarResponse = afterLoginPage.getWebResponse().getContentAsString();

        bankSession = new BankSession();

        JsonReader reader = Json.createReader(new StringReader(passwordAndAvatarResponse));
        JsonObject userIdJson = reader.readObject();
        JsonArray userContextJsonArray = userIdJson.getJsonArray("userContexts");
        bankSession.userId = userContextJsonArray.getJsonObject(0).getInt("id");

        bankSession.sessionToken = afterLoginPage.getWebResponse().getResponseHeaderValue("Session-Token");
    }

    private String writeJsonToString(JsonObject jsonObject) {
        Writer writer = new StringWriter();
        Json.createWriter(writer).write(jsonObject);
        return writer.toString();
    }

    private void checkPasswordLength(String password, int minLength) {
        if (minLength > password.length())
            // entered password is shorter than expected
            throw new BadPasswordException();
    }

    //TODO method name too long
    private int[] extractMaskedPasswordKeysIndexesFromResponse() {
        JsonReader reader = Json.createReader(new StringReader(loginResponse));

        JsonObject loginResponseJson = reader.readObject();

        if (!loginResponseJson.getString("loginProcess").equals("PARTIAL_PASSWORD"))
            throw new BadLoginMethodException();

        JsonArray maskedPasswordKeysJsonArray = loginResponseJson.getJsonArray("passwordKeys");
        int[] maskedPasswordKeysIndexes =
                maskedPasswordKeysJsonArray
                .getValuesAs(JsonNumber.class)
                .stream()
                .mapToInt(JsonNumber::intValue)
                .toArray();
        return maskedPasswordKeysIndexes;
    }

    //TODO variables names
    private String buildMaskedPassword(int[] passwordKeysIntArr, UserCredentials userCredentials) {
        JsonObjectBuilder passwordJsonBuilder = Json.createObjectBuilder();
        JsonObjectBuilder maskedPasswordJsonBuilder = Json.createObjectBuilder();

        for (int passwordKeyIdx : passwordKeysIntArr) {
            maskedPasswordJsonBuilder.add(String.valueOf(passwordKeyIdx),
                    userCredentials.password.substring(passwordKeyIdx - 1, passwordKeyIdx));
        }

        passwordJsonBuilder.add("login", userCredentials.login)
                .add("maskedPassword", maskedPasswordJsonBuilder)
                .add("avatarId", userCredentials.avatarId)
                .add("loginScopeType", "WWW");

        JsonObject maskedPasswordJson = passwordJsonBuilder.build();
        return writeJsonToString(maskedPasswordJson);
    }
}