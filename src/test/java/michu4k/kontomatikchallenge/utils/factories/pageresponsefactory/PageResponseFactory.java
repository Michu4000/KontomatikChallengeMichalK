package michu4k.kontomatikchallenge.utils.factories.pageresponsefactory;

import com.gargoylesoftware.htmlunit.Page;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import michu4k.kontomatikchallenge.stubs.PageStub;
import michu4k.kontomatikchallenge.utils.PageResponseBodyProvider;
import michu4k.kontomatikchallenge.utils.UrlProvider;

public class PageResponseFactory {
    public static Page getPageBadLogin() throws IOException {
        return getPage(UrlProvider.LOGIN_SITE_URL, PageResponseBodyProvider.getBadLogin());
    }

    public static Page getPageValidLogin(boolean maskedPasswordMethod, int passwordLength, int[] maskedPasswordKeysIndexes) throws IOException {
        String responseBody;
        if(maskedPasswordMethod)
            responseBody = buildValidLoginMaskedPasswordResponseBody(passwordLength, maskedPasswordKeysIndexes);
        else
            responseBody = PageResponseBodyProvider.getValidLoginBeginning() + PageResponseBodyProvider.getValidLoginEndForNotMaskedPassword();
        return getPage(UrlProvider.LOGIN_SITE_URL, responseBody);
    }

    public static Page getPageValidPasswordAndAvatar(String sessionToken, int userId) throws IOException {
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Session-Token", sessionToken);
        String responseBody = PageResponseBodyProvider.getValidPasswordAndAvatarBeginning() + userId + PageResponseBodyProvider.getValidPasswordAndAvatarEnd();
        return getPage(UrlProvider.PASSWORD_AND_AVATAR_SITE_URL, responseHeaders, responseBody);
    }

    public static Page getPageBadPasswordAndAvatar() throws IOException {
        return getPage(UrlProvider.PASSWORD_AND_AVATAR_SITE_URL, PageResponseBodyProvider.getBadPasswordAndAvatar());
    }

    public static Page getPageValidBankAccounts(int userId) throws IOException {
        String url = UrlProvider.BANK_ACCOUNTS_SITE_URL_BEGINNING + userId + UrlProvider.BANK_ACCOUNTS_SITE_URL_END;
        return getPage(url, PageResponseBodyProvider.getValidBankAccounts());
    }

    public static Page getPageBadUrl(String url) throws IOException { // http 404
        return getPage(url, "BAD URL");
    }

    private static String buildValidLoginMaskedPasswordResponseBody(int passwordLength, int[] maskedPasswordKeysIndexes) throws IOException {
        return PageResponseBodyProvider.getValidLoginBeginning() + "\"passwordLength\":" + passwordLength +
            ",\"passwordKeys\":" + Arrays.toString(maskedPasswordKeysIndexes) + ",\"loginProcess\":\"PARTIAL_PASSWORD\"}";
    }

    static Page getPage(String url, String responseBody) throws IOException {
        return getPage(url, Collections.emptyMap(), responseBody);
    }

    private static Page getPage(String url, Map<String, String> responseHeaders, String responseBody) throws IOException {
        return new PageStub(url, responseHeaders, responseBody);
    }
}