package michu4k.kontomatikchallenge.stubs;

import com.gargoylesoftware.htmlunit.WebResponse;

import java.util.Map;

class WebResponseStub extends WebResponse {
    private final Map<String, String> responseHeaders;
    private final String responseBody;

    WebResponseStub(Map<String, String> responseHeaders, String responseBody) {
        super(null, null, 10);
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
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