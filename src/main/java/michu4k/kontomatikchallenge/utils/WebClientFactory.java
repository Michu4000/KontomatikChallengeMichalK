package michu4k.kontomatikchallenge.utils;

import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;

public class WebClientFactory {
    //TODO remove static webClient - it's not thread safe
    private static WebClient webClient;

    public static WebClient getWebClient() {
        createAndSetWebClient();
        return webClient;
    }

    public static WebClient getWebClientWithProxy() {
        createAndSetWebClientWithProxy();
        return webClient;
    }

    private static void createAndSetWebClient() {
        webClient = new WebClient();
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
    }

    private static void createAndSetWebClientWithProxy() {
        createAndSetWebClient();
        ProxyConfig proxyConfig = new ProxyConfig("127.0.0.1", 8080);
        webClient.getOptions().setProxyConfig(proxyConfig);
        webClient.getOptions().setUseInsecureSSL(true);
    }
}