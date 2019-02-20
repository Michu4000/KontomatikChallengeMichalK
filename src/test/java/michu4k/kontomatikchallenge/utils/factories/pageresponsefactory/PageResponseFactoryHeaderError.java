package michu4k.kontomatikchallenge.utils.factories.pageresponsefactory;

import com.gargoylesoftware.htmlunit.Page;
import michu4k.kontomatikchallenge.utils.PageResponseBodyProvider;

import java.io.IOException;

public class PageResponseFactoryHeaderError {
    public static Page getPageBadHttpMethod(String url) throws IOException { // http 405
        return PageResponseFactory.getPage(url, "BAD HTTP METHOD");
    }

    public static Page getPageBadContentType(String url) throws IOException { // http 415
        return PageResponseFactory.getPage(url, "BAD CONTENT TYPE");
    }

    public static Page getPageBadReferer(String url) throws IOException { // ignored by server, it's working normal way
        return PageResponseFactory.getPage(url, "BAD REFERER");
    }

    public static Page getPageBadSessionToken(String url) throws IOException { // http 401
        return PageResponseFactory.getPage(url, PageResponseBodyProvider.getBadSessionToken());
    }

    public static Page getPageBadAcceptHeader(String url) throws IOException { // ignored by server, it's working normal way
        return PageResponseFactory.getPage(url, "BAD ACCEPT HEADER");
    }

    public static Page getPageBadAcceptEncoding(String url) throws IOException { // ignored by server, it's working normal way
        return PageResponseFactory.getPage(url, "BAD ACCEPT ENCODING");
    }

    public static Page getPageBadAcceptLanguage(String url) throws IOException { // ignored by server, it's working normal way
        return PageResponseFactory.getPage(url, "BAD ACCEPT LANGUAGE");
    }
}