package michu4k.kontomatikchallenge;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

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

    private String login;
    private String password;
    private String avatarId;
    private WebClient webClient;
    private String sessionToken;
    private String userId;

    PartialPasswordBankAuthenticator(String login, String pass, String avatarId, WebClient webClient) {
        this.login = login;
        this.password = pass;
        this.avatarId = avatarId;
        this.webClient = webClient;
    }

    @Override
    public void logIntoAccount() {
        final String loginResponse = typeLogin();
        final int[] passwordKeysIntArr = extractPartialPasswordKeysFromResponse(loginResponse);

        // entered password is shorter than expected
        if(passwordKeysIntArr[passwordKeysIntArr.length-1] > password.length()) {
            UserInterface.printBadPasswordError();
            System.exit(2);
        }

        sendPasswordAndAvatar(passwordKeysIntArr);
    }

    private String typeLogin() {
        URL loginSiteUrl = null;
        try {
            loginSiteUrl = new URL(LOGIN_SITE_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        final String loginSendRequestBody = new StringBuilder("{\"login\":\"")
                .append(login)
                .append("\"}").toString();
        final WebRequest loginSendRequest = WebRequestFactory.produceRequestPost(loginSiteUrl, loginSendRequestBody);

        Page passwordPage = null;
        try {
            passwordPage = webClient.getPage(loginSendRequest);
        } catch (FailingHttpStatusCodeException e) {
            e.printStackTrace();
            UserInterface.printBadLoginError();
            System.exit(2);
        } catch (IOException e) {
            e.printStackTrace();
            UserInterface.printConnectionError();
            System.exit(1);
        }
        return passwordPage.getWebResponse().getContentAsString();
    }

    private void sendPasswordAndAvatar(int[] passwordKeysIntArr) {
        final String passAndAvatarSendRequestBody = buildMaskedPassword(passwordKeysIntArr);
        URL passwordAndAvatarUrl = null;
        try {
            passwordAndAvatarUrl = new URL(PASSWORD_AND_AVATAR_SITE_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        final WebRequest passAndAvatarSendRequest = WebRequestFactory.produceRequestPost(passwordAndAvatarUrl,
                passAndAvatarSendRequestBody);

        Page afterLoginPage = null;
        try {
            afterLoginPage = webClient.getPage(passAndAvatarSendRequest);
        } catch (FailingHttpStatusCodeException e) {
            e.printStackTrace();
            UserInterface.printCredentialsError();
            System.exit(2);
        } catch (IOException e2) {
            e2.printStackTrace();
            UserInterface.printConnectionError();
            System.exit(1);
        }
        final String passwordAndAvatarResponse = afterLoginPage.getWebResponse().getContentAsString();

        final Pattern userIdPattern = Pattern.compile("\"userContexts\":\\[\\{\"id\":(.*?),");
        final Matcher userIdMatcher = userIdPattern.matcher(passwordAndAvatarResponse);
        if (userIdMatcher.find()) {
            userId = userIdMatcher.group(1);
        }
        else {
            UserInterface.printConnectionError();
            System.exit(1);
        }
        sessionToken = afterLoginPage.getWebResponse().getResponseHeaderValue("Session-Token");
    }

    private int[] extractPartialPasswordKeysFromResponse(String loginResponse) {
        final Pattern passwordKeysPattern = Pattern.compile("\"passwordKeys\":\\[(.*)\\]");
        final Matcher passwordKeysMatcher = passwordKeysPattern.matcher(loginResponse);
        String passwordKeys = null;
        if (passwordKeysMatcher.find()) {
            passwordKeys = passwordKeysMatcher.group(1);
        }
        else {
            UserInterface.printConnectionError();
            System.exit(1);
        }
        final String[] passwordKeysStrArr = passwordKeys.split(",");
        final Stream<String> passwordKeysStream = Arrays.stream(passwordKeysStrArr);
        return passwordKeysStream.mapToInt(x -> Integer.parseInt(x)).toArray();
    }

    private String buildMaskedPassword(int[] passwordKeysIntArr) {
        final StringBuilder maskedPassBuilder = new StringBuilder("{\"login\":\"");
        maskedPassBuilder.append(login)
                .append("\",\"maskedPassword\":{");
        for (int passKeyIdx : passwordKeysIntArr) {
            maskedPassBuilder.append("\"")
                    .append(passKeyIdx)
                    .append("\":\"")
                    .append(password.charAt(passKeyIdx - 1))
                    .append("\",");
        }
        maskedPassBuilder.delete(maskedPassBuilder.length() - 1, maskedPassBuilder.length())
                .append("},\"avatarId\":")
                .append(avatarId)
                .append(",\"loginScopeType\":\"WWW\"}");
        return  maskedPassBuilder.toString();
    }

    @Override
    public String getSessionToken() {
        return sessionToken;
    }

    @Override
    public String getUserId() {
        return userId;
    }
}
