package michu4k.kontomatikchallenge.bankauthentication;

import michu4k.kontomatikchallenge.structures.BankSession;
import michu4k.kontomatikchallenge.structures.UserCredentials;
import michu4k.kontomatikchallenge.exceptions.BadCredentialsException;
import michu4k.kontomatikchallenge.exceptions.BadLoginNameException;
import michu4k.kontomatikchallenge.exceptions.BadLoginMethodException;
import michu4k.kontomatikchallenge.utils.JsonUtils;
import michu4k.kontomatikchallenge.utils.PasswordUtils;
import michu4k.kontomatikchallenge.utils.UrlProvider;
import michu4k.kontomatikchallenge.utils.WebRequestFactory;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import javax.json.JsonObject;
import javax.json.Json;
import javax.json.JsonArray;

import java.io.IOException;
import java.net.URL;

public class NestBankMaskedPasswordAuthenticator implements BankAuthenticator {
    private final WebClient webClient;

    public NestBankMaskedPasswordAuthenticator(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public BankSession logIntoBankAccount(UserCredentials userCredentials) throws IOException {
        String loginResponse = enterLogin(userCredentials.login);
        BankSession bankSession = enterPasswordAndAvatar(loginResponse, userCredentials);
        return bankSession;
    }

    private String enterLogin(String loginName) throws IOException {
        try {
            WebRequest loginRequest = createLoginRequest(loginName);
            String loginResponse = sendLoginRequestAndGetResponse(loginRequest);
            checkLoginMethod(loginResponse);
            return loginResponse;
        } catch (FailingHttpStatusCodeException failingHttpStatusCodeException) {
            throw new BadLoginNameException(failingHttpStatusCodeException);
        }
    }

    private BankSession enterPasswordAndAvatar(String loginResponse, UserCredentials userCredentials) throws IOException {
        try {
            WebRequest passwordAndAvatarRequest = createPasswordAndAvatarRequest(loginResponse, userCredentials);
            Page signedInPage = sendPasswordAndAvatarRequestAndGetSignedInPage(passwordAndAvatarRequest);
            String passwordAndAvatarResponse = extractPasswordAndAvatarResponseFromPage(signedInPage);
            String sessionToken = extractSessionTokenFromPage(signedInPage);
            return createBankSession(passwordAndAvatarResponse, sessionToken);
        } catch (FailingHttpStatusCodeException failingHttpStatusCodeException) {
            throw new BadCredentialsException(failingHttpStatusCodeException);
        }
    }

    private WebRequest createLoginRequest(String loginName) throws IOException {
        URL loginSiteUrl = new URL(UrlProvider.LOGIN_SITE_URL);
        JsonObject jsonLogin = Json.createObjectBuilder().add("login", loginName).build();
        String jsonLoginString = JsonUtils.writeJsonToString(jsonLogin);
        return WebRequestFactory.createRequestPost(loginSiteUrl, jsonLoginString);
    }

    private String sendLoginRequestAndGetResponse(WebRequest loginRequest) throws IOException {
        Page passwordPage = webClient.getPage(loginRequest);
        return passwordPage.getWebResponse().getContentAsString();
    }

    private void checkLoginMethod(String loginResponse) {
        JsonObject loginResponseJson = JsonUtils.parseStringToJson(loginResponse);
        if (!isLoginMethodMaskedPassword(loginResponseJson))
            throw new BadLoginMethodException();
    }

    private WebRequest createPasswordAndAvatarRequest(String loginResponse, UserCredentials userCredentials) throws IOException {
        URL passwordAndAvatarSiteUrl = new URL(UrlProvider.PASSWORD_AND_AVATAR_SITE_URL);
        int[] maskedPasswordKeysIndexes = PasswordUtils.extractMaskedPasswordKeysIndexesFromResponse(loginResponse);
        PasswordUtils.checkPasswordLength(userCredentials.password, maskedPasswordKeysIndexes);
        String passwordAndAvatarRequestBody = PasswordUtils.buildPasswordAndAvatarRequestBody(maskedPasswordKeysIndexes, userCredentials);
        return WebRequestFactory.createRequestPost(passwordAndAvatarSiteUrl, passwordAndAvatarRequestBody);
    }

    private Page sendPasswordAndAvatarRequestAndGetSignedInPage(WebRequest passwordAndAvatarRequest) throws IOException {
        return webClient.getPage(passwordAndAvatarRequest);
    }

    private String extractPasswordAndAvatarResponseFromPage(Page signedInPage) {
        return signedInPage.getWebResponse().getContentAsString();
    }

    private String extractSessionTokenFromPage(Page signedInPage) {
        return signedInPage.getWebResponse().getResponseHeaderValue("Session-Token");
    }

    private BankSession createBankSession(String passwordAndAvatarResponse, String sessionToken) {
        BankSession bankSession = new BankSession();
        JsonArray userContextJsonArray = JsonUtils.parseStringToJsonArray(passwordAndAvatarResponse, "userContexts");
        bankSession.userId = userContextJsonArray.getJsonObject(0).getInt("id");
        bankSession.sessionToken = sessionToken;
        return bankSession;
    }

    private boolean isLoginMethodMaskedPassword(JsonObject loginResponseJson) {
        return loginResponseJson.getString("loginProcess").equals("PARTIAL_PASSWORD");
    }
}