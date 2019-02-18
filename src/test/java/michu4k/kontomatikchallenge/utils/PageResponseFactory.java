package michu4k.kontomatikchallenge.utils;

import com.gargoylesoftware.htmlunit.Page;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import michu4k.kontomatikchallenge.stubs.PageStub;

public class PageResponseFactory {
    public static Page getPageBadLogin() throws IOException {
        return getPage(UrlProvider.LOGIN_SITE_URL, PageResponseBodyProvider.getBadLogin());
    }

    public static Page getPageValidLogin(boolean maskedPasswordMethod, int passwordLength, int[] maskedPasswordKeysIndexes) throws IOException {
        String responseBody;
        if(maskedPasswordMethod) {
            responseBody = PageResponseBodyProvider.getValidLoginBeginning() + "\"passwordLength\":" + passwordLength +
                    ",\"passwordKeys\":" + Arrays.toString(maskedPasswordKeysIndexes) + ",\"loginProcess\":\"PARTIAL_PASSWORD\"}";
        } else {
            responseBody = PageResponseBodyProvider.getValidLoginBeginning() + PageResponseBodyProvider.getValidLoginEnd();
        }
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
        String urlAddress = UrlProvider.BANK_ACCOUNTS_SITE_URL_BEGINNING + userId + UrlProvider.BANK_ACCOUNTS_SITE_URL_END;
        return getPage(urlAddress, PageResponseBodyProvider.getValidBankAccounts());
    }

    public static Page getPageBadUrl() throws IOException { // http 404
        String urlAddress = "https://bad.url.com";
        return getPage(urlAddress, "BAD URL");
    }

    static Page getPage(String urlAddress, String responseBody) throws IOException {
        return getPage(urlAddress, new HashMap<>(), responseBody);
    }

    private static Page getPage(String urlAddress, Map<String, String> responseHeaders, String responseBody) throws IOException {
        return new PageStub(urlAddress, responseHeaders, responseBody);
    }
}