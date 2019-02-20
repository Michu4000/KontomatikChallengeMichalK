package michu4k.kontomatikchallenge.stubs;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.WebWindow;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class PageStub implements Page {
    private URL url;
    private Map<String, String> responseHeaders;
    private String responseBody;

    public PageStub(String url, Map<String, String> responseHeaders, String responseBody) throws IOException {
        this.url = new URL(url);
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    @Override
    public void initialize() throws IOException {}

    @Override
    public void cleanUp() {}

    @Override
    public WebResponse getWebResponse() {
        return new WebResponseStub(responseHeaders, responseBody);
    }

    @Override
    public WebWindow getEnclosingWindow() {
        return null;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public boolean isHtmlPage() {
        return true;
    }
}