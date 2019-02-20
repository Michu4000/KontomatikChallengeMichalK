package michu4k.kontomatikchallenge.stubs.webclientstub;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import michu4k.kontomatikchallenge.utils.UrlProvider;

import java.io.IOException;

public class WebClientStub extends WebClient {
    public final String validLoginName = "testtest";
    public final String validPassword = "testtest123";
    public final int validAvatarId = 23;
    public final String validSessionToken =
        "GSAaq9o7oEEPKhzlPM4WMOn8YrKq+VMmTzjy/RkLQdFmY0IqEOMkmFA+Nb2NiqMY12riHsfiGBZjEv3SsYSY8CtLfyJhnf/gWg2HK4AA" +
        "m72zCvDAS4POmw5nQc+sC/FySYAfVqJ+YlFt5dv0XPjMUV4bSEcUwpdjhNpryKo/peLaAgybSq4xUzD/7u2i2g/OabvCDly/2ZYE0egj" +
        "6PIV/h8x1HyrZAyknjrUqE6pCf1bdOAdu5goiTnNwuicYy3J";
    public final int validUserId = 10250313;

    final int[] validMaskedPasswordKeysIndexes = {1, 3, 7};
    final boolean maskedPasswordMethod = true;

    @Override
    public <P extends Page> P getPage(WebRequest request) throws IOException, FailingHttpStatusCodeException {
        String BankAccountSiteUrl = UrlProvider.BANK_ACCOUNTS_SITE_URL_BEGINNING + validUserId + UrlProvider.BANK_ACCOUNTS_SITE_URL_END;
        String requestUrl = request.getUrl().toString();

        if (requestUrl.equals(UrlProvider.LOGIN_SITE_URL))
            return (P) WebClientStubRequestHandler.handleLoginRequest(request, this);
        else if (requestUrl.equals(UrlProvider.PASSWORD_AND_AVATAR_SITE_URL))
            return (P) WebClientStubRequestHandler.handlePasswordAndAvatarRequest(request, this);
        else if (requestUrl.equals(BankAccountSiteUrl))
            return (P) WebClientStubRequestHandler.handleBankAccountsRequest(request, this);
        else
            return (P) WebClientStubRequestHandler.handleBadUrlError(request.getUrl().toString());
    }
}