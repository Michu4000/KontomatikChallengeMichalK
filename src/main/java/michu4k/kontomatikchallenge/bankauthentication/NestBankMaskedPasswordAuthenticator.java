package michu4k.kontomatikchallenge.bankauthentication;

import michu4k.kontomatikchallenge.datastructures.BankSession;
import michu4k.kontomatikchallenge.datastructures.UserCredentials;
import michu4k.kontomatikchallenge.exceptions.BadCredentialsException;
import michu4k.kontomatikchallenge.exceptions.BadLoginException;
import michu4k.kontomatikchallenge.exceptions.BadPasswordException;
import michu4k.kontomatikchallenge.utils.WebRequestFactory;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.Json;

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
        Json.createWriter(writer).write(loginJson);
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

        //TODO use library to parse JSONs
        Pattern userIdPattern = Pattern.compile("\"userContexts\":\\[\\{\"id\":(.*?),");
        Matcher userIdMatcher = userIdPattern.matcher(passwordAndAvatarResponse);
        if (userIdMatcher.find())
            try {
                bankSession.userId = Integer.parseInt(userIdMatcher.group(1));
            } catch (NumberFormatException numberFormatException) {
                throw new IOException(numberFormatException);
            }
        else
            throw new IOException();
        bankSession.sessionToken = afterLoginPage.getWebResponse().getResponseHeaderValue("Session-Token");
        return bankSession;
    }

    //TODO method name too long
    private int[] extractMaskedPasswordKeysIndexesFromResponse(String loginResponse) throws IOException {
        //TODO use library to parse JSONs
        Pattern maskedPasswordKeysIndexesPattern = Pattern.compile("\"passwordKeys\":\\[(.*)\\]");
        Matcher maskedPasswordKeysIndexesMatcher = maskedPasswordKeysIndexesPattern.matcher(loginResponse);
        String rawMaskedPasswordKeysIndexes;
        if (maskedPasswordKeysIndexesMatcher.find())
            rawMaskedPasswordKeysIndexes = maskedPasswordKeysIndexesMatcher.group(1);
        else
            throw new IOException();

        String[] maskedPasswordKeysIndexes = rawMaskedPasswordKeysIndexes.split(",");
        Stream<String> maskedPasswordKeysIndexesStream = Arrays.stream(maskedPasswordKeysIndexes);

        try {
            return maskedPasswordKeysIndexesStream.mapToInt(x -> Integer.parseInt(x)).toArray();
        } catch (NumberFormatException numberFormatException) {
            throw new IOException(numberFormatException);
        }
    }

    //TODO variables names
    private String buildMaskedPassword(int[] passwordKeysIntArr, UserCredentials userCredentials) {
        //TODO use library to build JSONs
        //TODO hint: i can concat Strings because it's more clear and this loop is short so won't matter

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
        Json.createWriter(writer).write(maskedPasswordJson);
        return writer.toString();
    }
}
