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

public class PartialPasswordBankAuthenticator implements BankAuthenticator {
    private final static String LOGIN_SITE_URL = "https://login.nestbank.pl/rest/v1/auth/checkLogin";
    private final static String PASSWORD_AND_AVATAR_SITE_URL =
            "https://login.nestbank.pl/rest/v1/auth/loginByPartialPassword";

    private WebClient webClient;

    public PartialPasswordBankAuthenticator(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public BankSession logIntoAccount(UserCredentials userCredentials)
            throws BankConnectionException, BadCredentialsException {
        String loginResponse = typeLogin(userCredentials);
        int[] passwordKeysIntArr = extractPartialPasswordKeysFromResponse(loginResponse);

        // entered password is shorter than expected
        if(passwordKeysIntArr[passwordKeysIntArr.length-1] > userCredentials.getPasswordLength()) {
            throw new BadPasswordException();
        }

        return sendPasswordAndAvatar(passwordKeysIntArr, userCredentials);
    }

    private String typeLogin(UserCredentials userCredentials) throws BankConnectionException, BadLoginException {
        URL loginSiteUrl = null;
        try {
            loginSiteUrl = new URL(LOGIN_SITE_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String loginSendRequestBody = new StringBuilder("{\"login\":\"")
                .append(userCredentials.getLogin())
                .append("\"}").toString();
        WebRequest loginSendRequest = WebRequestFactory.produceRequestPost(loginSiteUrl, loginSendRequestBody);

        Page passwordPage;
        try {
            passwordPage = webClient.getPage(loginSendRequest);
        } catch (FailingHttpStatusCodeException e) {
            e.printStackTrace();
            throw new BadLoginException();
        } catch (IOException e) {
            e.printStackTrace();
            throw new BankConnectionException();
        }
        return passwordPage.getWebResponse().getContentAsString();
    }

    private BankSession sendPasswordAndAvatar(int[] passwordKeysIntArr, UserCredentials userCredentials)
            throws BankConnectionException, BadCredentialsException {
        String passAndAvatarSendRequestBody = buildMaskedPassword(passwordKeysIntArr, userCredentials);
        URL passwordAndAvatarUrl = null;
        try {
            passwordAndAvatarUrl = new URL(PASSWORD_AND_AVATAR_SITE_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        WebRequest passAndAvatarSendRequest = WebRequestFactory.produceRequestPost(passwordAndAvatarUrl,
                passAndAvatarSendRequestBody);

        Page afterLoginPage;
        try {
            afterLoginPage = webClient.getPage(passAndAvatarSendRequest);
        } catch (FailingHttpStatusCodeException e) {
            e.printStackTrace();
            throw new BadCredentialsException();
        } catch (IOException e2) {
            e2.printStackTrace();
            throw new BankConnectionException();
        }
        String passwordAndAvatarResponse = afterLoginPage.getWebResponse().getContentAsString();

        BankSession bankSession = new BankSession();

        Pattern userIdPattern = Pattern.compile("\"userContexts\":\\[\\{\"id\":(.*?),");
        Matcher userIdMatcher = userIdPattern.matcher(passwordAndAvatarResponse);
        if (userIdMatcher.find()) {
            bankSession.setUserId(Integer.parseInt(userIdMatcher.group(1)));
        }
        else {
            throw new BankConnectionException();
        }
        bankSession.setSessionToken(afterLoginPage.getWebResponse().
                getResponseHeaderValue("Session-Token"));
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
        StringBuilder maskedPassBuilder = new StringBuilder("{\"login\":\"");
        maskedPassBuilder.append(userCredentials.getLogin())
                .append("\",\"maskedPassword\":{");
        for (int passKeyIdx : passwordKeysIntArr) {
            maskedPassBuilder.append("\"")
                    .append(passKeyIdx)
                    .append("\":\"")
                    .append(userCredentials.getPassword().charAt(passKeyIdx - 1))
                    .append("\",");
        }
        maskedPassBuilder.delete(maskedPassBuilder.length() - 1, maskedPassBuilder.length())
                .append("},\"avatarId\":")
                .append(userCredentials.getAvatarId())
                .append(",\"loginScopeType\":\"WWW\"}");
        return  maskedPassBuilder.toString();
    }
}
