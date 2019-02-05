package michu4k.kontomatikchallenge.bankauthentication;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import michu4k.kontomatikchallenge.datastructures.BankSession;
import michu4k.kontomatikchallenge.datastructures.UserCredentials;
import michu4k.kontomatikchallenge.userinterface.ErrorsPrinter;
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

    private UserCredentials userCredentials;
    private WebClient webClient;
    private BankSession bankSession;

    public PartialPasswordBankAuthenticator(UserCredentials userCredentials, WebClient webClient) {
        this.userCredentials = userCredentials;
        this.webClient = webClient;
    }

    @Override
    public BankSession logIntoAccount() {
        String loginResponse = typeLogin();
        int[] passwordKeysIntArr = extractPartialPasswordKeysFromResponse(loginResponse);

        // entered password is shorter than expected
        if(passwordKeysIntArr[passwordKeysIntArr.length-1] > userCredentials.getPasswordLength()) {
            ErrorsPrinter.printBadPasswordError();
            System.exit(2);
        } // TODO throw exception

        return sendPasswordAndAvatar(passwordKeysIntArr);
    }

    private String typeLogin() {
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

        Page passwordPage = null;
        try {
            passwordPage = webClient.getPage(loginSendRequest);
        } catch (FailingHttpStatusCodeException e) {
            e.printStackTrace();
            ErrorsPrinter.printBadLoginError();
            System.exit(2);
        } catch (IOException e) {
            e.printStackTrace();
            ErrorsPrinter.printConnectionError();
            System.exit(1);
        }
        return passwordPage.getWebResponse().getContentAsString();
    }

    private BankSession sendPasswordAndAvatar(int[] passwordKeysIntArr) {
        String passAndAvatarSendRequestBody = buildMaskedPassword(passwordKeysIntArr);
        URL passwordAndAvatarUrl = null;
        try {
            passwordAndAvatarUrl = new URL(PASSWORD_AND_AVATAR_SITE_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        WebRequest passAndAvatarSendRequest = WebRequestFactory.produceRequestPost(passwordAndAvatarUrl,
                passAndAvatarSendRequestBody);

        Page afterLoginPage = null;
        try {
            afterLoginPage = webClient.getPage(passAndAvatarSendRequest);
        } catch (FailingHttpStatusCodeException e) {
            e.printStackTrace();
            ErrorsPrinter.printCredentialsError();
            System.exit(2);
        } catch (IOException e2) {
            e2.printStackTrace();
            ErrorsPrinter.printConnectionError();
            System.exit(1);
        }
        String passwordAndAvatarResponse = afterLoginPage.getWebResponse().getContentAsString();

        bankSession = new BankSession();

        Pattern userIdPattern = Pattern.compile("\"userContexts\":\\[\\{\"id\":(.*?),");
        Matcher userIdMatcher = userIdPattern.matcher(passwordAndAvatarResponse);
        if (userIdMatcher.find()) {
            bankSession.setUserId(Integer.parseInt(userIdMatcher.group(1)));
        }
        else {
            ErrorsPrinter.printConnectionError();
            System.exit(1);
        }
        bankSession.setSessionToken(afterLoginPage.getWebResponse().getResponseHeaderValue("Session-Token"));
        return bankSession;
    }

    private int[] extractPartialPasswordKeysFromResponse(String loginResponse) {
        Pattern passwordKeysPattern = Pattern.compile("\"passwordKeys\":\\[(.*)\\]");
        Matcher passwordKeysMatcher = passwordKeysPattern.matcher(loginResponse);
        String passwordKeys = null;
        if (passwordKeysMatcher.find()) {
            passwordKeys = passwordKeysMatcher.group(1);
        }
        else {
            ErrorsPrinter.printConnectionError();
            System.exit(1);
        }
        String[] passwordKeysStrArr = passwordKeys.split(",");
        Stream<String> passwordKeysStream = Arrays.stream(passwordKeysStrArr);
        return passwordKeysStream.mapToInt(x -> Integer.parseInt(x)).toArray();
    }

    private String buildMaskedPassword(int[] passwordKeysIntArr) {
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
