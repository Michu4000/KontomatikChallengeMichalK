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
import michu4k.kontomatikchallenge.utils.WebRequestFactory;

import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

//TODO change class name, something like "MaskedPassword"
public class NestBankPartialPasswordAuthenticator implements BankAuthenticator {
    private final static String LOGIN_SITE_URL = "https://login.nestbank.pl/rest/v1/auth/checkLogin";
    private final static String PASSWORD_AND_AVATAR_SITE_URL =
            "https://login.nestbank.pl/rest/v1/auth/loginByPartialPassword";

    private final WebClient webClient;

    public NestBankPartialPasswordAuthenticator(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public BankSession logIntoBankAccount(UserCredentials userCredentials) throws IOException {
        //TODO don't mix up different abstraction levels!
        //TODO should be: 1.enterLogin() 2.enterPassword() 3.enterAvatar()

        String loginResponse;
        try {
            loginResponse = enterLogin(userCredentials);
        } catch (FailingHttpStatusCodeException failingHttpStatusCodeException) {
            throw new BadLoginException(failingHttpStatusCodeException);
        }
        //TODO var name, should be maskedPasswordIndexes etc.
        int[] passwordKeysIntArr = extractPartialPasswordKeysFromResponse(loginResponse);

        //TODO as above: this shouldn't be here:

        // entered password is shorter than expected
        if (passwordKeysIntArr[passwordKeysIntArr.length-1] > userCredentials.getPasswordLength())
            throw new BadPasswordException();
        try {
            return enterPasswordAndAvatar(passwordKeysIntArr, userCredentials);
        } catch (FailingHttpStatusCodeException failingHttpStatusCodeException) {
            throw new BadCredentialsException(failingHttpStatusCodeException);
        }
    }

    //TODO argument should be only login
    private String enterLogin(UserCredentials userCredentials) throws IOException {
        URL loginSiteUrl = new URL(LOGIN_SITE_URL);

        //TODO use library to build JSONs
        String loginSendRequestBody = new StringBuilder("{\"login\":\"")
                .append(StringEscapeUtils.escapeJson(userCredentials.login))
                .append("\"}").toString();
        WebRequest loginSendRequest = WebRequestFactory.produceRequestPost(loginSiteUrl, loginSendRequestBody);

        Page passwordPage = webClient.getPage(loginSendRequest);

        return passwordPage.getWebResponse().getContentAsString();
    }

    private BankSession enterPasswordAndAvatar(int[] passwordKeysIntArr, UserCredentials userCredentials)
            throws IOException {
        String passwordAndAvatarSendRequestBody = buildMaskedPassword(passwordKeysIntArr, userCredentials);
        URL passwordAndAvatarUrl = new URL(PASSWORD_AND_AVATAR_SITE_URL);

        WebRequest passwordAndAvatarSendRequest = WebRequestFactory.produceRequestPost(passwordAndAvatarUrl,
                passwordAndAvatarSendRequestBody);

        Page afterLoginPage = webClient.getPage(passwordAndAvatarSendRequest);

        String passwordAndAvatarResponse = afterLoginPage.getWebResponse().getContentAsString();

        BankSession bankSession = new BankSession();

        //TODO use library to build JSONs
        Pattern userIdPattern = Pattern.compile("\"userContexts\":\\[\\{\"id\":(.*?),");
        Matcher userIdMatcher = userIdPattern.matcher(passwordAndAvatarResponse);
        if (userIdMatcher.find())
            bankSession.userId = Integer.parseInt(userIdMatcher.group(1));
        else
            throw new IOException();
        bankSession.sessionToken = afterLoginPage.getWebResponse().getResponseHeaderValue("Session-Token");
        return bankSession;
    }

    //TODO method name: PartialPassword to MaskedPassword etc.
    private int[] extractPartialPasswordKeysFromResponse(String loginResponse) throws IOException {
        //TODO use library to build JSONs
        //TODO var names
        Pattern passwordKeysPattern = Pattern.compile("\"passwordKeys\":\\[(.*)\\]");
        Matcher passwordKeysMatcher = passwordKeysPattern.matcher(loginResponse);
        String passwordKeys;
        if (passwordKeysMatcher.find())
            passwordKeys = passwordKeysMatcher.group(1);
        else
            throw new IOException();

        //TODO var names
        String[] passwordKeysStrArr = passwordKeys.split(",");
        Stream<String> passwordKeysStream = Arrays.stream(passwordKeysStrArr);

        return passwordKeysStream.mapToInt(x -> Integer.parseInt(x)).toArray();
    }

    private String buildMaskedPassword(int[] passwordKeysIntArr, UserCredentials userCredentials) {
        //TODO use library to build JSONs
        //TODO hint: i can concat Strings because it's more clear and this loop is short so won't matter
        StringBuilder maskedPasswordBuilder = new StringBuilder("{\"login\":\"");
        maskedPasswordBuilder.append(StringEscapeUtils.escapeJson(userCredentials.login))
                .append("\",\"maskedPassword\":{");
        for (int passwordKeyIdx : passwordKeysIntArr) {
            maskedPasswordBuilder.append("\"")
                    .append(passwordKeyIdx)
                    .append("\":\"")
                    .append(StringEscapeUtils.escapeJson(userCredentials.password.substring(passwordKeyIdx-1, passwordKeyIdx)))
                    .append("\",");
        }
        maskedPasswordBuilder.delete(maskedPasswordBuilder.length() - 1, maskedPasswordBuilder.length())
                .append("},\"avatarId\":")
                .append(userCredentials.avatarId)
                .append(",\"loginScopeType\":\"WWW\"}");
        return  maskedPasswordBuilder.toString();
    }
}
