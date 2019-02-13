package michu4k.kontomatikchallenge.stubs;

import com.gargoylesoftware.htmlunit.WebResponse;

import java.util.Map;

public class WebResponseStub extends WebResponse {

    private String responseBody;
    private Map<String, String> responseHeaders;

    public WebResponseStub(Map<String, String> responseHeaders, String responseBody) {
        super(null, null, 10);
        this.responseBody = responseBody;
        this.responseHeaders = responseHeaders;
    }

    @Override
    public String getContentAsString(){
        return responseBody;
    }

    @Override
    public String getResponseHeaderValue(String headerName) {
        return responseHeaders.get(headerName);
    }
}
