package michu4k.kontomatikchallenge.bankauthentication;

import michu4k.kontomatikchallenge.datastructures.BankSession;
import michu4k.kontomatikchallenge.datastructures.UserCredentials;
import michu4k.kontomatikchallenge.exceptions.BadCredentialsException;
import michu4k.kontomatikchallenge.exceptions.BadLoginException;
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

    public NestBankMaskedPasswordAuthenticator(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public BankSession logIntoBankAccount(UserCredentials userCredentials) throws IOException {
        //TODO don't mix up different abstraction levels!
        //TODO should be: 1.enterLogin() 2.enterPassword() 3.enterAvatar()

        String loginResponse;
        try {
            loginResponse = enterLogin(userCredentials.login);
        } catch (FailingHttpStatusCodeException failingHttpStatusCodeException) {
            throw new BadLoginException(failingHttpStatusCodeException);
        }

        int[] maskedPasswordKeysIndexes = extractMaskedPasswordKeysIndexesFromResponse(loginResponse);

        //TODO as above: this shouldn't be here:

        // entered password is shorter than expected
        if (maskedPasswordKeysIndexes[maskedPasswordKeysIndexes.length-1] > userCredentials.password.length())
            throw new BadPasswordException();
        try {
            return enterPasswordAndAvatar(maskedPasswordKeysIndexes, userCredentials);
        } catch (FailingHttpStatusCodeException failingHttpStatusCodeException) {
            throw new BadCredentialsException(failingHttpStatusCodeException);
        }
    }

    private String enterLogin(String login) throws IOException {
        URL loginSiteUrl = new URL(LOGIN_SITE_URL);

        JsonObject loginJson = Json.createObjectBuilder().add("login", login).build();
        Writer writer = new StringWriter();
        try {
            Json.createWriter(writer).write(loginJson);
        } catch (JsonException | IllegalStateException jsonException) {
            throw new IOException(jsonException);
        }
        String loginJsonString = writer.toString();

        WebRequest loginSendRequest = WebRequestFactory.createRequestPost(loginSiteUrl, loginJsonString);
        Page passwordPage = webClient.getPage(loginSendRequest);
        return passwordPage.getWebResponse().getContentAsString();
    }

    private BankSession enterPasswordAndAvatar(int[] passwordKeysIntArr, UserCredentials userCredentials)
            throws IOException {
        String passwordAndAvatarSendRequestBody = buildMaskedPassword(passwordKeysIntArr, userCredentials);
        URL passwordAndAvatarUrl = new URL(PASSWORD_AND_AVATAR_SITE_URL);

        WebRequest passwordAndAvatarSendRequest = WebRequestFactory.createRequestPost(passwordAndAvatarUrl,
                passwordAndAvatarSendRequestBody);

        Page afterLoginPage = webClient.getPage(passwordAndAvatarSendRequest);

        String passwordAndAvatarResponse = afterLoginPage.getWebResponse().getContentAsString();

        BankSession bankSession = new BankSession();

        JsonReader reader = Json.createReader(new StringReader(passwordAndAvatarResponse));
        try {
            JsonObject userIdJson = reader.readObject();
            JsonArray userContextJsonArray = userIdJson.getJsonArray("userContexts");
            bankSession.userId = userContextJsonArray.getJsonObject(0).getInt("id");
        } catch (JsonException | IllegalStateException | ClassCastException jsonException) {
            throw new IOException(jsonException);
        }

        bankSession.sessionToken = afterLoginPage.getWebResponse().getResponseHeaderValue("Session-Token");
        return bankSession;
    }

    //TODO method name too long
    private int[] extractMaskedPasswordKeysIndexesFromResponse(String loginResponse) throws IOException {
        JsonReader reader = Json.createReader(new StringReader(loginResponse));

        try {
            JsonObject loginResponseJson = reader.readObject();

            if (!loginResponseJson.getString("loginProcess").equals("PARTIAL_PASSWORD"))
                throw new BadLoginMethodException();

            JsonArray maskedPasswordKeysJsonArray = loginResponseJson.getJsonArray("passwordKeys");
            int[] maskedPasswordKeysIndexes = maskedPasswordKeysJsonArray.getValuesAs(JsonNumber.class).stream().mapToInt(JsonNumber::intValue).toArray();
            return maskedPasswordKeysIndexes;
        } catch (JsonException | IllegalStateException | ClassCastException jsonException) {
            throw new IOException(jsonException);
        }
    }

    //TODO variables names
    private String buildMaskedPassword(int[] passwordKeysIntArr, UserCredentials userCredentials) throws IOException {
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

        Writer writer = new StringWriter();
        try {
            Json.createWriter(writer).write(maskedPasswordJson);
        } catch (JsonException | IllegalStateException jsonException) {
            throw new IOException(jsonException);
        }
        return writer.toString();
    }
}