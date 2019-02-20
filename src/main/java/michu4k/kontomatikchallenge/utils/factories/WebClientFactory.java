package michu4k.kontomatikchallenge.utils.factories;

import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;

public class WebClientFactory {
    public static WebClient getWebClient() {
        WebClient webClient = new WebClient();
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        return webClient;
    }

    public static WebClient getWebClientWithProxy() {
        WebClient webClient = getWebClient();
        ProxyConfig proxyConfig = new ProxyConfig("127.0.0.1", 8080);
        webClient.getOptions().setProxyConfig(proxyConfig);
        webClient.getOptions().setUseInsecureSSL(true);
        return webClient;
    }
}