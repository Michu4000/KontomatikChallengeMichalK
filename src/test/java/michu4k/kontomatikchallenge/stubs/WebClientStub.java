package michu4k.kontomatikchallenge.stubs;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;

import michu4k.kontomatikchallenge.utils.JsonUtils;
import michu4k.kontomatikchallenge.utils.PageResponseFactory;
import michu4k.kontomatikchallenge.utils.PasswordUtils;
import michu4k.kontomatikchallenge.utils.UrlProvider;

import javax.json.JsonObject;

import java.io.IOException;

//TODO split into 2 classes (or more)

public class WebClientStub extends WebClient {
    private final static String DEFAULT_LOGIN_NAME = "testtest";
    private final static String DEFAULT_PASSWORD = "testtest123";
    private final static int DEFAULT_AVATAR_ID = 23;
    private final static int[] DEFAULT_MASKED_PASSWORD_KEYS_INDEXES = {1, 3, 7};
    private final static int DEFAULT_USER_ID = 10250313;
    private final static String DEFAULT_SESSION_TOKEN =
            "GSAaq9o7oEEPKhzlPM4WMOn8YrKq+VMmTzjy/RkLQdFmY0IqEOMkmFA+Nb2NiqMY12riHsfiGBZjEv3SsYSY8CtLfyJhnf/gWg2HK4AA" +
            "m72zCvDAS4POmw5nQc+sC/FySYAfVqJ+YlFt5dv0XPjMUV4bSEcUwpdjhNpryKo/peLaAgybSq4xUzD/7u2i2g/OabvCDly/2ZYE0egj" +
            "6PIV/h8x1HyrZAyknjrUqE6pCf1bdOAdu5goiTnNwuicYy3J";

    public String validLoginName = DEFAULT_LOGIN_NAME;
    public String validPassword = DEFAULT_PASSWORD;
    public int validAvatarId = DEFAULT_AVATAR_ID;
    public int[] validMaskedPasswordKeysIndexes = DEFAULT_MASKED_PASSWORD_KEYS_INDEXES;
    public boolean maskedPasswordMethod = true;
    public String validSessionToken = DEFAULT_SESSION_TOKEN;

    private int validUserId = DEFAULT_USER_ID;
    private String BankAccountSiteUrl;
    private WebRequest request;
    private Page outputPage;

    public WebClientStub() {
        setBankAccountSite(DEFAULT_USER_ID);
    }

    public void setUserId(int userId) {
        validUserId = userId;
        setBankAccountSite(userId);
    }

    public int getUserId() {
        return validUserId;
    }

    private void setBankAccountSite(int userId) {
        BankAccountSiteUrl = UrlProvider.BANK_ACCOUNTS_SITE_URL_BEGINNING + userId + UrlProvider.BANK_ACCOUNTS_SITE_URL_END;
    }

    @Override
    public <P extends Page> P getPage(WebRequest request) throws IOException, FailingHttpStatusCodeException {
        this.request = request;
        String requestUrl = request.getUrl().toString();
        if (requestUrl.equals(UrlProvider.LOGIN_SITE_URL))
            handleLoginRequest();
        else if (requestUrl.equals(UrlProvider.PASSWORD_AND_AVATAR_SITE_URL))
            handlePasswordAndAvatarRequest();
        else if (requestUrl.equals(BankAccountSiteUrl))
            handleBankAccountsRequest();
        else
            handleBadUrlError();
        return (P) outputPage;
    }

    private void handleLoginRequest() throws IOException {
        if(!checkHeadersInRequestPost())
            return;
        if(checkLoginRequestBody(request.getRequestBody()))
            outputPage = PageResponseFactory.getPageValidLogin(maskedPasswordMethod, validPassword.length(), validMaskedPasswordKeysIndexes);
        else
            outputPage = PageResponseFactory.getPageBadLogin();
    }

    private void handlePasswordAndAvatarRequest() throws IOException {
        if(!checkHeadersInRequestPost())
            return;
        if(checkPasswordAndAvatarRequestBody(request.getRequestBody()))
            outputPage = PageResponseFactory.getPageValidPasswordAndAvatar(validSessionToken, validUserId);
        else
            outputPage = PageResponseFactory.getPageBadPasswordAndAvatar();
    }

    private void handleBankAccountsRequest() throws IOException {
        if(checkHeadersInRequestGet())
            outputPage = PageResponseFactory.getPageValidBankAccounts(validUserId);
    }

    private void handleBadUrlError() throws IOException {
        outputPage = PageResponseFactory.getPageBadUrl();
    }

    private boolean checkHeadersInRequestPost() throws IOException {
        if(!checkCommonHeaders())
            return false;
        if(!request.getHttpMethod().equals(HttpMethod.POST)) {
            outputPage = PageResponseFactory.getPageBadHttpMethod();
            return false;
        }
        if(!request.getAdditionalHeaders().get("Content-Type").equals("application/json")) {
            outputPage = PageResponseFactory.getPageBadContentType();
            return false;
        }
        if(!request.getAdditionalHeaders().get("Referer").equals("https://login.nestbank.pl/login")) {
            outputPage = PageResponseFactory.getPageBadReferer();
            return false;
        }
        return true;
    }

    private boolean checkHeadersInRequestGet() throws IOException {
        if(!checkCommonHeaders())
            return false;
        if(!request.getHttpMethod().equals(HttpMethod.GET)) {
            outputPage = PageResponseFactory.getPageBadHttpMethod();
            return false;
        }
        if(!request.getAdditionalHeaders().get("Content-Type").equals("text/plain")) {
            outputPage = PageResponseFactory.getPageBadContentType();
            return false;
        }
        if(!request.getAdditionalHeaders().get("Referer").equals("https://login.nestbank.pl/dashboard/products")) {
            outputPage = PageResponseFactory.getPageBadReferer();
            return false;
        }
        if(!request.getAdditionalHeaders().get("Session-Token").equals(validSessionToken)) {
            outputPage = PageResponseFactory.getPageBadSessionToken(validUserId);
            return false;
        }
        return true;
    }

    private boolean checkCommonHeaders() throws IOException {
        if(!request.getAdditionalHeaders().get("Accept").equals("*/*")) {
            outputPage = PageResponseFactory.getPageBadAcceptHeader();
            return false;
        }
        if(!request.getAdditionalHeaders().get("Accept-Encoding").equals("gzip, deflate")) {
            outputPage = PageResponseFactory.getPageBadAcceptEncoding();
            return false;
        }
        if(!request.getAdditionalHeaders().get("Accept-Language").equals("en-US,en;q=0.9,pl;q=0.8")) {
            outputPage = PageResponseFactory.getPageBadAcceptLanguage();
            return false;
        }
        return true;
    }

    private boolean checkLoginRequestBody(String requestBody) {
        // default valid request json: {"login":"testtest"}
        JsonObject jsonObject = JsonUtils.parseStringToJson(requestBody);
        boolean isLoginNameValid = jsonObject.getString("login").equals(validLoginName);
        return isLoginNameValid;
    }

    private boolean checkPasswordAndAvatarRequestBody(String requestBody) {
        // default valid request json:
        // {"login":"testtest","maskedPassword":{"1":"t","3":"s","7":"s"},"avatarId":23,"loginScopeType":"WWW"}
        JsonObject jsonObject = JsonUtils.parseStringToJson(requestBody);
        if (!jsonObject.getString("login").equals(validLoginName))
            return false;
        JsonObject maskedPasswordJson = PasswordUtils.buildMaskedPassword(validMaskedPasswordKeysIndexes, validPassword).build();
        if (!jsonObject.getJsonObject("maskedPassword").equals(maskedPasswordJson))
            return false;
        if (jsonObject.getInt("avatarId") != validAvatarId)
            return false;
        if (!jsonObject.getString("loginScopeType").equals("WWW"))
            return false;
        return true;
    }
}