package michu4k.kontomatikchallenge.bankauthentication;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import michu4k.kontomatikchallenge.datastructures.BankSession;
import michu4k.kontomatikchallenge.datastructures.UserCredentials;
import michu4k.kontomatikchallenge.exceptions.BadCredentialsException;
import michu4k.kontomatikchallenge.exceptions.BadLoginException;
import michu4k.kontomatikchallenge.exceptions.BadPasswordException;
import michu4k.kontomatikchallenge.exceptions.BankConnectionException;
import michu4k.kontomatikchallenge.utils.WebRequestFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class NestBankPartialPasswordAuthenticator implements BankAuthenticator {
    private final static String LOGIN_SITE_URL = "https://login.nestbank.pl/rest/v1/auth/checkLogin";
    private final static String PASSWORD_AND_AVATAR_SITE_URL =
            "https://login.nestbank.pl/rest/v1/auth/loginByPartialPassword";

    private WebClient webClient;

    public NestBankPartialPasswordAuthenticator(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public BankSession logIntoBankAccount(UserCredentials userCredentials)
            throws BankConnectionException, BadCredentialsException, MalformedURLException {
        String loginResponse = enterLogin(userCredentials);
        int[] passwordKeysIntArr = extractPartialPasswordKeysFromResponse(loginResponse);

        // entered password is shorter than expected
        if (passwordKeysIntArr[passwordKeysIntArr.length-1] > userCredentials.getPasswordLength()) {
            throw new BadPasswordException();
        }

        return enterPasswordAndAvatar(passwordKeysIntArr, userCredentials);
    }

    private String enterLogin(UserCredentials userCredentials) throws BankConnectionException, BadLoginException, MalformedURLException {
        URL loginSiteUrl = new URL(LOGIN_SITE_URL);

        String loginSendRequestBody = new StringBuilder("{\"login\":\"")
                .append(userCredentials.login) //TODO escape characters which can allow dependency injection attack like " { } etc.
                .append("\"}").toString();
        WebRequest loginSendRequest = WebRequestFactory.produceRequestPost(loginSiteUrl, loginSendRequestBody);

        Page passwordPage;
        try {
            passwordPage = webClient.getPage(loginSendRequest);
        } catch (FailingHttpStatusCodeException e) {
            BadLoginException badLoginException = new BadLoginException();
            badLoginException.setStackTrace(e.getStackTrace());
            throw badLoginException;
        } catch (IOException e2) {
            BankConnectionException bankConnectionException = new BankConnectionException();
            bankConnectionException.setStackTrace(e2.getStackTrace());
            throw  bankConnectionException;
        }
        return passwordPage.getWebResponse().getContentAsString();
    }

    private BankSession enterPasswordAndAvatar(int[] passwordKeysIntArr, UserCredentials userCredentials)
            throws BankConnectionException, BadCredentialsException, MalformedURLException {
        String passwordAndAvatarSendRequestBody = buildMaskedPassword(passwordKeysIntArr, userCredentials);
        URL passwordAndAvatarUrl = new URL(PASSWORD_AND_AVATAR_SITE_URL);

        WebRequest passwordAndAvatarSendRequest = WebRequestFactory.produceRequestPost(passwordAndAvatarUrl,
                passwordAndAvatarSendRequestBody);

        Page afterLoginPage;
        try {
            afterLoginPage = webClient.getPage(passwordAndAvatarSendRequest);
        } catch (FailingHttpStatusCodeException e) {
            BadCredentialsException badCredentialsException = new BadCredentialsException();
            badCredentialsException.setStackTrace(e.getStackTrace());
            throw badCredentialsException;
        } catch (IOException e2) {
            BankConnectionException bankConnectionException = new BankConnectionException();
            bankConnectionException.setStackTrace(e2.getStackTrace());
            throw  bankConnectionException;
        }
        String passwordAndAvatarResponse = afterLoginPage.getWebResponse().getContentAsString();

        BankSession bankSession = new BankSession();

        Pattern userIdPattern = Pattern.compile("\"userContexts\":\\[\\{\"id\":(.*?),");
        Matcher userIdMatcher = userIdPattern.matcher(passwordAndAvatarResponse);
        if (userIdMatcher.find()) {
            bankSession.userId = Integer.parseInt(userIdMatcher.group(1));
        }
        else {
            throw new BankConnectionException();
        }
        bankSession.sessionToken = afterLoginPage.getWebResponse().getResponseHeaderValue("Session-Token");
        return bankSession;
    }

    private int[] extractPartialPasswordKeysFromResponse(String loginResponse) throws BankConnectionException {
        Pattern passwordKeysPattern = Pattern.compile("\"passwordKeys\":\\[(.*)\\]");
        Matcher passwordKeysMatcher = passwordKeysPattern.matcher(loginResponse);
        String passwordKeys;
        if (passwordKeysMatcher.find()) {
            passwordKeys = passwordKeysMatcher.group(1);
        }
        else {
            throw new BankConnectionException();
        }
        String[] passwordKeysStrArr = passwordKeys.split(",");
        Stream<String> passwordKeysStream = Arrays.stream(passwordKeysStrArr);
        try {
            return passwordKeysStream.mapToInt(x -> Integer.parseInt(x)).toArray();
        } catch (NumberFormatException e) {
            throw new BankConnectionException();
        }
    }

    private String buildMaskedPassword(int[] passwordKeysIntArr, UserCredentials userCredentials) {
        StringBuilder maskedPasswordBuilder = new StringBuilder("{\"login\":\"");
        maskedPasswordBuilder.append(userCredentials.login) //TODO escape characters which can allow dependency injection attack like " { } etc.
                .append("\",\"maskedPassword\":{");
        for (int passwordKeyIdx : passwordKeysIntArr) {
            maskedPasswordBuilder.append("\"")
                    .append(passwordKeyIdx)
                    .append("\":\"")
                    .append(userCredentials.password.charAt(passwordKeyIdx - 1)) //TODO escape characters which can allow dependency injection attack like " { } etc.
                    .append("\",");
        }
        maskedPasswordBuilder.delete(maskedPasswordBuilder.length() - 1, maskedPasswordBuilder.length())
                .append("},\"avatarId\":")
                .append(userCredentials.avatarId)
                .append(",\"loginScopeType\":\"WWW\"}");
        return  maskedPasswordBuilder.toString();
    }
}
