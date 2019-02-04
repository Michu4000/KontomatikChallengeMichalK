package michu4k.kontomatikchallenge.utils;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebRequest;

import java.net.URL;

public class WebRequestFactory {
    private final static String REQUEST_POST_REFERER_URL = "https://login.nestbank.pl/dashboard/products";
    private final static String REQUEST_POST_CONTENT_TYPE = "application/json";
    private final static String REQUEST_GET_REFERER_URL = "https://login.nestbank.pl/dashboard/products";
    private final static String REQUEST_GET_CONTENT_TYPE = "text/plain";
    private final static String REQUEST_ACCEPT_ENCODING = "gzip, deflate";
    private final static String REQUEST_ACCEPT_LANGUAGE = "en-US,en;q=0.9,pl;q=0.8";

    public static WebRequest produceRequestPost(URL url, String requestContent) {
        final WebRequest requestPost = addCommonHeaders(new WebRequest(url, HttpMethod.POST));
        requestPost.setAdditionalHeader("Content-Type", REQUEST_POST_CONTENT_TYPE);
        requestPost.setAdditionalHeader("Referer", REQUEST_POST_REFERER_URL);
        requestPost.setRequestBody(requestContent);
        return requestPost;
    }

    public static WebRequest produceRequestGet(URL url, String sessionToken) {
        final WebRequest requestGet = addCommonHeaders(new WebRequest(url, HttpMethod.GET));
        requestGet.setAdditionalHeader("Content-Type", REQUEST_GET_CONTENT_TYPE);
        requestGet.setAdditionalHeader("Referer", REQUEST_GET_REFERER_URL);
        requestGet.setAdditionalHeader("Session-Token", sessionToken);
        return requestGet;
    }

    private static WebRequest addCommonHeaders(WebRequest webRequest) {
        webRequest.setAdditionalHeader("Accept", "*/*");
        webRequest.setAdditionalHeader("Accept-Encoding", REQUEST_ACCEPT_ENCODING);
        webRequest.setAdditionalHeader("Accept-Language", REQUEST_ACCEPT_LANGUAGE);
        return webRequest;
    }
}
