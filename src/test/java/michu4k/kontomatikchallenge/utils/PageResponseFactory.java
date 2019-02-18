package michu4k.kontomatikchallenge.utils;

import com.gargoylesoftware.htmlunit.Page;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import michu4k.kontomatikchallenge.stubs.PageStub;

//TODO split into 2 classes (or more)

public class PageResponseFactory {
    public static Page getPageBadLogin() throws IOException {
        return getPage(UrlProvider.LOGIN_SITE_URL, PageResponseBodyProvider.BAD_LOGIN);
    }

    public static Page getPageValidLogin(boolean maskedPasswordMethod, int passwordLength, int[] maskedPasswordKeysIndexes) throws IOException {
        String responseBody;
        if(maskedPasswordMethod) {
            responseBody = PageResponseBodyProvider.VALID_LOGIN_BEGINNING + "\"passwordLength\":" + passwordLength +
                    ",\"passwordKeys\":" + Arrays.toString(maskedPasswordKeysIndexes) + ",\"loginProcess\":\"PARTIAL_PASSWORD\"}";
        } else {
            responseBody = PageResponseBodyProvider.VALID_LOGIN_BEGINNING + PageResponseBodyProvider.VALID_LOGIN_METHOD_FULL_PASSWORD;
        }
        return getPage(UrlProvider.LOGIN_SITE_URL, responseBody);
    }

    public static Page getPageValidPasswordAndAvatar(String sessionToken, int userId) throws IOException {
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Session-Token", sessionToken);
        String responseBody = PageResponseBodyProvider.VALID_PASSWORD_AND_AVATAR_BEGINNING + userId + PageResponseBodyProvider.VALID_PASSWORD_AND_AVATAR_END;
        return getPage(UrlProvider.PASSWORD_AND_AVATAR_SITE_URL, responseHeaders, responseBody);
    }

    public static Page getPageBadPasswordAndAvatar() throws IOException {
        return getPage(UrlProvider.PASSWORD_AND_AVATAR_SITE_URL, PageResponseBodyProvider.BAD_PASSWORD_AND_AVATAR);
    }

    public static Page getPageValidBankAccounts(int userId) throws IOException {
        String urlAddress = UrlProvider.BANK_ACCOUNTS_SITE_URL_BEGINNING + userId + UrlProvider.BANK_ACCOUNTS_SITE_URL_END;
        return getPage(urlAddress, PageResponseBodyProvider.VALID_BANK_ACCOUNTS);
    }

    public static Page getPageBadUrl() throws IOException { // http 404
        String urlAddress = "https://bad.url.com";
        return getPage(urlAddress, "BAD URL");
    }

    public static Page getPageBadHttpMethod() throws IOException { // http 405
        String urlAddress = "https://bad.http.method.com";
        return getPage(urlAddress, "BAD HTTP METHOD");
    }

    public static Page getPageBadContentType() throws IOException { // http 415
        String urlAddress = "https://bad.content.type.com";
        return getPage(urlAddress, "BAD CONTENT TYPE");
    }

    public static Page getPageBadReferer() throws IOException { // ignored by server, it's working normal way
        String urlAddress = "https://bad.referer.com";
        return getPage(urlAddress, "BAD REFERER");
    }

    public static Page getPageBadSessionToken(int userId) throws IOException { // http 401
        String urlAddress = UrlProvider.BANK_ACCOUNTS_SITE_URL_BEGINNING + userId + UrlProvider.BANK_ACCOUNTS_SITE_URL_END;
        return getPage(urlAddress, PageResponseBodyProvider.BAD_SESSION_TOKEN);
    }

    public static Page getPageBadAcceptHeader() throws IOException { // ignored by server, it's working normal way
        String urlAddress = "https://bad.accept.com";
        return getPage(urlAddress, "BAD ACCEPT HEADER");
    }

    public static Page getPageBadAcceptEncoding() throws IOException { // ignored by server, it's working normal way
        String urlAddress = "https://bad.accept.encoding.com";
        return getPage(urlAddress, "BAD ACCEPT ENCODING");
    }

    public static Page getPageBadAcceptLanguage() throws IOException { // ignored by server, it's working normal way
        String urlAddress = "https://bad.accept.language.com";
        return getPage(urlAddress, "BAD ACCEPT LANGUAGE");
    }

    private static Page getPage(String urlAddress, String responseBody) throws IOException {
        return getPage(urlAddress, new HashMap<>(), responseBody);
    }

    private static Page getPage(String urlAddress, Map<String, String> responseHeaders, String responseBody) throws IOException {
        return new PageStub(urlAddress, responseHeaders, responseBody);
    }
}