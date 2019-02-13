package michu4k.kontomatikchallenge.stubs;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;

import michu4k.kontomatikchallenge.utils.PasswordUtils;

import javax.json.JsonReader;
import javax.json.Json;
import javax.json.JsonObject;

import java.io.IOException;
import java.io.StringReader;

public class WebClientStub extends WebClient {

    private final static String LOGIN_SITE_URL = "https://login.nestbank.pl/rest/v1/auth/checkLogin";
    private final static String PASSWORD_AND_AVATAR_SITE_URL =
            "https://login.nestbank.pl/rest/v1/auth/loginByPartialPassword";
    private final static String BANK_ACCOUNTS_DATA_SITE_URL_BEGINNING = "https://login.nestbank.pl/rest/v1/context/";
    private final static String BANK_ACCOUNTS_DATA_SITE_URL_END = "/dashboard/www/config";
    private String BankAccountSiteUrl;

    private final static String DEFAULT_LOGIN_NAME = "testtest";
    private final static String DEFAULT_PASSWORD = "testtest123";
    private final static int DEFAULT_AVATAR_ID = 13;
    private final static int[] DEFAULT_MASKED_PASSWORD_KEYS_INDEXES = {1, 3, 7};
    private final static int DEFAULT_USER_ID = 10250313;
    private final static String DEFAULT_SESSION_TOKEN = "GSAaq9o7oEEPKhzlPM4WMOn8YrKq+VMmTzjy/RkLQdFmY0IqEOMkmFA+Nb2N" +
            "iqMY12riHsfiGBZjEv3SsYSY8CtLfyJhnf/gWg2HK4AAm71zCvDAS4POmw5nQc+sC/FySYAfVqJ+YlFt5dv0XPjMUV4bSEcUwpdjhNpr" +
            "yKo/peIaAgybSq4xUzD/7u2i2g/OaavCDly/2ZYE0egj6PIV/h8x1HyrZAyknjrUqE6pCf1bdOAdu5goiTnNwuicYy3J";

    public String validLoginName = DEFAULT_LOGIN_NAME;
    public String validPassword = DEFAULT_PASSWORD;
    public int validAvatarId = DEFAULT_AVATAR_ID;
    public int[] validMaskedPasswordKeysIndexes = DEFAULT_MASKED_PASSWORD_KEYS_INDEXES;
    public boolean maskedPasswordMethod = true;
    private int validUserId = DEFAULT_USER_ID;
    public String validSessionToken = DEFAULT_SESSION_TOKEN;

    private WebRequest request;
    private Page outputPage;

    public WebClientStub() {
        BankAccountSiteUrl = BANK_ACCOUNTS_DATA_SITE_URL_BEGINNING
                + DEFAULT_USER_ID
                + BANK_ACCOUNTS_DATA_SITE_URL_END;
    }

    private void setUserId(int userId) {
        validUserId = userId;
        BankAccountSiteUrl = BANK_ACCOUNTS_DATA_SITE_URL_BEGINNING + userId + BANK_ACCOUNTS_DATA_SITE_URL_END;
    }

    private int getUserId() {
        return validUserId;
    }

    @Override
    public <P extends Page> P getPage(WebRequest request) throws IOException, FailingHttpStatusCodeException {
        this.request = request;
        String requestUrl = request.getUrl().toString();

        if (requestUrl.equals(LOGIN_SITE_URL))
            handleLoginRequest();
        else if (requestUrl.equals(PASSWORD_AND_AVATAR_SITE_URL))
            handlePasswordAndAvatarRequest();
        else if (requestUrl.equals(BankAccountSiteUrl))
            handleBankAccountsRequest();
        else handleBadUrlError();

        return (P) outputPage;
    }

    private void handleLoginRequest() {
        checkCommonHeaders();
        checkHeadersInRequestPost();

        if(checkLoginRequestBody(request.getRequestBody()))
            ;//send good response
        else
            ;//send bad response
    }

    private void handlePasswordAndAvatarRequest() {
        checkCommonHeaders();
        checkHeadersInRequestPost();

        if(checkPasswordAndAvatarRequestBody(request.getRequestBody()))
            ;//send good response
        else
            ;//send bad response
    }

    private void handleBankAccountsRequest() {
        checkCommonHeaders();
        checkHeadersInRequestGet();
    }

    private void handleBadUrlError() {
        //bad url response
    }

    private void checkCommonHeaders() {
        if(!request.getAdditionalHeaders().get("Accept").equals("*/*"))
            ;//bad accept
        if(!request.getAdditionalHeaders().get("Accept-Encoding").equals("gzip, deflate"))
            ;//bad accept-encoding
        if(!request.getAdditionalHeaders().get("Accept-Language").equals("en-US,en;q=0.9,pl;q=0.8"))
            ;//bad accept-language
    }

    private void checkHeadersInRequestPost() {
        if(!request.getHttpMethod().equals(HttpMethod.POST))
            ;//bad http method
        if(!request.getAdditionalHeaders().get("Content-Type").equals("application/json"))
            ;//bad content-type
        if(!request.getAdditionalHeaders().get("Referer").equals("https://login.nestbank.pl/login"))
            ;//bad referer
    }

    private void checkHeadersInRequestGet() {
        if(!request.getHttpMethod().equals(HttpMethod.GET))
            ;//bad http method
        if(!request.getAdditionalHeaders().get("Content-Type").equals("text/plain"))
            ;//bad content-type
        if(!request.getAdditionalHeaders().get("Referer").equals("https://login.nestbank.pl/dashboard/products"))
            ;//bad referer
        if(!request.getAdditionalHeaders().get("Session-Token").equals(validSessionToken))
            ;//bad session-token
    }

    private boolean checkLoginRequestBody(String requestBody) {
        JsonReader reader = Json.createReader(new StringReader(requestBody));
        JsonObject jsonObject = reader.readObject();
        return jsonObject.getString("login").equals(validLoginName);
    }

    private boolean checkPasswordAndAvatarRequestBody(String requestBody) {
        //{"login":"testtest","maskedPassword":{"2":"b","3":"c","4":"d","11":"k","12":"l"},"avatarId":7,"loginScopeType":"WWW"}
        JsonReader reader = Json.createReader(new StringReader(requestBody));
        JsonObject jsonObject = reader.readObject();
        if (!jsonObject.getString("login").equals(validLoginName))
            ;//badLogin

        JsonObject maskedPasswordJson =
                PasswordUtils.buildMaskedPassword(validMaskedPasswordKeysIndexes, validPassword).build();
        if (!jsonObject.getJsonObject("maskedPassword").equals(maskedPasswordJson))
            ;//badMaskedPassword

        if (jsonObject.getInt("avatarId") != validAvatarId)
            ;//badAvatarId
        if (!jsonObject.getString("loginScopeType").equals("WWW"))
            ;//badLoginScopeType
        return true;
    }
}