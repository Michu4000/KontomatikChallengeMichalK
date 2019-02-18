package michu4k.kontomatikchallenge.utils;

import com.gargoylesoftware.htmlunit.Page;

import java.io.IOException;

public class PageResponseFactoryHeaderError {
    public static Page getPageBadHttpMethod() throws IOException { // http 405
        String urlAddress = "https://bad.http.method.com";
        return PageResponseFactory.getPage(urlAddress, "BAD HTTP METHOD");
    }

    public static Page getPageBadContentType() throws IOException { // http 415
        String urlAddress = "https://bad.content.type.com";
        return PageResponseFactory.getPage(urlAddress, "BAD CONTENT TYPE");
    }

    public static Page getPageBadReferer() throws IOException { // ignored by server, it's working normal way
        String urlAddress = "https://bad.referer.com";
        return PageResponseFactory.getPage(urlAddress, "BAD REFERER");
    }

    public static Page getPageBadSessionToken(int userId) throws IOException { // http 401
        String urlAddress = UrlProvider.BANK_ACCOUNTS_SITE_URL_BEGINNING + userId + UrlProvider.BANK_ACCOUNTS_SITE_URL_END;
        return PageResponseFactory.getPage(urlAddress, PageResponseBodyProvider.getBadSessionToken());
    }

    public static Page getPageBadAcceptHeader() throws IOException { // ignored by server, it's working normal way
        String urlAddress = "https://bad.accept.com";
        return PageResponseFactory.getPage(urlAddress, "BAD ACCEPT HEADER");
    }

    public static Page getPageBadAcceptEncoding() throws IOException { // ignored by server, it's working normal way
        String urlAddress = "https://bad.accept.encoding.com";
        return PageResponseFactory.getPage(urlAddress, "BAD ACCEPT ENCODING");
    }

    public static Page getPageBadAcceptLanguage() throws IOException { // ignored by server, it's working normal way
        String urlAddress = "https://bad.accept.language.com";
        return PageResponseFactory.getPage(urlAddress, "BAD ACCEPT LANGUAGE");
    }
}