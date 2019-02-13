package michu4k.kontomatikchallenge.bankauthentication;

import michu4k.kontomatikchallenge.datastructures.BankSession;
import michu4k.kontomatikchallenge.datastructures.UserCredentials;
import michu4k.kontomatikchallenge.exceptions.BadCredentialsException;
import michu4k.kontomatikchallenge.exceptions.BadLoginNameException;
import michu4k.kontomatikchallenge.exceptions.BadLoginMethodException;
import michu4k.kontomatikchallenge.utils.JsonUtils;
import michu4k.kontomatikchallenge.utils.PasswordUtils;
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

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

public class NestBankMaskedPasswordAuthenticator implements BankAuthenticator {
    private final static String LOGIN_SITE_URL = "https://login.nestbank.pl/rest/v1/auth/checkLogin";
    private final static String PASSWORD_AND_AVATAR_SITE_URL =
            "https://login.nestbank.pl/rest/v1/auth/loginByPartialPassword";

    private final WebClient webClient;
    private WebRequest loginRequest;
    private WebRequest passwordAndAvatarRequest;
    private String loginResponse;
    private String passwordAndAvatarResponse;
    private String sessionToken;
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
        } catch (JsonException | IllegalStateException | NullPointerException jsonException) {
            throw new IOException(jsonException);
        }

        try {
            enterPasswordAndAvatar(userCredentials);
        } catch (FailingHttpStatusCodeException failingHttpStatusCodeException) {
            throw new BadCredentialsException(failingHttpStatusCodeException);
        } catch (JsonException | IllegalStateException | ClassCastException | NullPointerException jsonException) {
            throw new IOException(jsonException);
        }

        return bankSession;
    }

    private void enterLogin(String login) throws IOException {
        createLoginRequest(login);
        sendLoginRequest();
        checkLoginMethod();
    }

    private void enterPasswordAndAvatar(UserCredentials userCredentials) throws IOException {
        createPasswordAndAvatarRequest(userCredentials);
        sendPasswordAndAvatarRequest();
        createBankSession();
    }

    private void createLoginRequest(String login) throws IOException {
        URL loginSiteUrl = new URL(LOGIN_SITE_URL);
        JsonObject jsonLogin = Json.createObjectBuilder().add("login", login).build();
        String jsonLoginString = JsonUtils.writeJsonToString(jsonLogin);
        loginRequest = WebRequestFactory.createRequestPost(loginSiteUrl, jsonLoginString);
    }

    private void sendLoginRequest() throws IOException {
        Page passwordPage = webClient.getPage(loginRequest);
        loginResponse = passwordPage.getWebResponse().getContentAsString();
    }

    private void checkLoginMethod() {
        JsonReader reader = Json.createReader(new StringReader(loginResponse));
        JsonObject loginResponseJson = reader.readObject();
        if (!loginResponseJson.getString("loginProcess").equals("PARTIAL_PASSWORD"))
            // login method other than masked password is not supported
            throw new BadLoginMethodException();
    }

    private void createPasswordAndAvatarRequest(UserCredentials userCredentials) throws IOException {
        URL passwordAndAvatarSiteUrl = new URL(PASSWORD_AND_AVATAR_SITE_URL);
        int[] maskedPasswordKeysIndexes = PasswordUtils.extractMaskedPasswordKeysIndexesFromResponse(loginResponse);
        PasswordUtils.checkPasswordLength(userCredentials.password, maskedPasswordKeysIndexes);
        String passwordAndAvatarRequestBody =
                PasswordUtils.buildPasswordAndAvatarRequestBody(maskedPasswordKeysIndexes, userCredentials);
        passwordAndAvatarRequest =
                WebRequestFactory.createRequestPost(passwordAndAvatarSiteUrl, passwordAndAvatarRequestBody);
    }

    private void sendPasswordAndAvatarRequest() throws IOException {
        Page signedInPage = webClient.getPage(passwordAndAvatarRequest);
        sessionToken = signedInPage.getWebResponse().getResponseHeaderValue("Session-Token");
        passwordAndAvatarResponse = signedInPage.getWebResponse().getContentAsString();
    }

    private void createBankSession() {
        bankSession = new BankSession();
        JsonArray userContextJsonArray =
                JsonUtils.parseResponseToJsonArray(passwordAndAvatarResponse, "userContexts");
        bankSession.userId = userContextJsonArray.getJsonObject(0).getInt("id");
        bankSession.sessionToken = sessionToken;
    }
}