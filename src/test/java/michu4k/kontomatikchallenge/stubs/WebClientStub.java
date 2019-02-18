package michu4k.kontomatikchallenge.stubs;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import michu4k.kontomatikchallenge.utils.UrlProvider;

import java.io.IOException;

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
        String requestUrl = request.getUrl().toString();
        if (requestUrl.equals(UrlProvider.LOGIN_SITE_URL))
            return (P) WebClientStubRequestHandler.handleLoginRequest(request, this);
        else if (requestUrl.equals(UrlProvider.PASSWORD_AND_AVATAR_SITE_URL))
            return (P) WebClientStubRequestHandler.handlePasswordAndAvatarRequest(request, this);
        else if (requestUrl.equals(BankAccountSiteUrl))
            return (P) WebClientStubRequestHandler.handleBankAccountsRequest(request, this);
        else
            return (P) WebClientStubRequestHandler.handleBadUrlError();
    }
}