package michu4k.kontomatikchallenge.stubs;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebRequest;

import michu4k.kontomatikchallenge.utils.JsonUtils;
import michu4k.kontomatikchallenge.utils.PageResponseFactory;
import michu4k.kontomatikchallenge.utils.PageResponseFactoryHeaderError;
import michu4k.kontomatikchallenge.utils.PasswordUtils;

import javax.json.JsonObject;
import java.io.IOException;

class WebClientStubRequestHandler {
    static Page handleLoginRequest(WebRequest request, WebClientStub webClientStub) throws IOException {
        Page headersError = checkHeadersInRequestPost(request);
        if(headersError != null)
            return headersError;
        if(checkLoginRequestBody(request.getRequestBody(), webClientStub.validLoginName))
            return PageResponseFactory.getPageValidLogin(
                    webClientStub.maskedPasswordMethod,
                    webClientStub.validPassword.length(),
                    webClientStub.validMaskedPasswordKeysIndexes
            );
        else
            return PageResponseFactory.getPageBadLogin();
    }

    static Page handlePasswordAndAvatarRequest(WebRequest request, WebClientStub webClientStub) throws IOException {
        Page headersError = checkHeadersInRequestPost(request);
        if(headersError != null)
            return headersError;
        if(checkPasswordAndAvatarRequestBody(request.getRequestBody(), webClientStub))
            return PageResponseFactory.getPageValidPasswordAndAvatar(webClientStub.validSessionToken, webClientStub.getUserId());
        else
            return PageResponseFactory.getPageBadPasswordAndAvatar();
    }

    static Page handleBankAccountsRequest(WebRequest request, WebClientStub webClientStub) throws IOException {
        Page headersError = checkHeadersInRequestGet(request, webClientStub);
        if(headersError != null)
            return headersError;
        else
            return PageResponseFactory.getPageValidBankAccounts(webClientStub.getUserId());
    }

    static Page handleBadUrlError() throws IOException {
        return PageResponseFactory.getPageBadUrl();
    }

    private static Page checkHeadersInRequestPost(WebRequest request) throws IOException {
        Page commonHeadersError = checkCommonHeaders(request);
        if(commonHeadersError != null)
            return commonHeadersError;
        if(!request.getHttpMethod().equals(HttpMethod.POST))
            return PageResponseFactoryHeaderError.getPageBadHttpMethod();
        if(!request.getAdditionalHeaders().get("Content-Type").equals("application/json"))
            return PageResponseFactoryHeaderError.getPageBadContentType();
        if(!request.getAdditionalHeaders().get("Referer").equals("https://login.nestbank.pl/login"))
            return PageResponseFactoryHeaderError.getPageBadReferer();
        return null;
    }

    private static Page checkHeadersInRequestGet(WebRequest request, WebClientStub webClientStub) throws IOException {
        Page commonHeadersError = checkCommonHeaders(request);
        if(commonHeadersError != null)
            return commonHeadersError;
        if(!request.getHttpMethod().equals(HttpMethod.GET))
            return PageResponseFactoryHeaderError.getPageBadHttpMethod();
        if(!request.getAdditionalHeaders().get("Content-Type").equals("text/plain"))
            return PageResponseFactoryHeaderError.getPageBadContentType();
        if(!request.getAdditionalHeaders().get("Referer").equals("https://login.nestbank.pl/dashboard/products"))
            return PageResponseFactoryHeaderError.getPageBadReferer();
        if(!request.getAdditionalHeaders().get("Session-Token").equals(webClientStub.validSessionToken))
            return PageResponseFactoryHeaderError.getPageBadSessionToken(webClientStub.getUserId());
        return null;
    }

    private static Page checkCommonHeaders(WebRequest request) throws IOException {
        if(!request.getAdditionalHeaders().get("Accept").equals("*/*"))
            return PageResponseFactoryHeaderError.getPageBadAcceptHeader();
        if(!request.getAdditionalHeaders().get("Accept-Encoding").equals("gzip, deflate"))
            return PageResponseFactoryHeaderError.getPageBadAcceptEncoding();
        if(!request.getAdditionalHeaders().get("Accept-Language").equals("en-US,en;q=0.9,pl;q=0.8"))
            return PageResponseFactoryHeaderError.getPageBadAcceptLanguage();
        return null;
    }

    private static boolean checkLoginRequestBody(String requestBody, String validLoginName) {
        // default valid request json: {"login":"testtest"}
        JsonObject jsonObject = JsonUtils.parseStringToJson(requestBody);
        boolean isLoginNameValid = jsonObject.getString("login").equals(validLoginName);
        return isLoginNameValid;
    }

    private static boolean checkPasswordAndAvatarRequestBody(String requestBody, WebClientStub webClientStub) {
        // default valid request json:
        // {"login":"testtest","maskedPassword":{"1":"t","3":"s","7":"s"},"avatarId":23,"loginScopeType":"WWW"}
        JsonObject jsonObject = JsonUtils.parseStringToJson(requestBody);
        if (!jsonObject.getString("login").equals(webClientStub.validLoginName))
            return false;
        JsonObject maskedPasswordJson = PasswordUtils.buildMaskedPassword(webClientStub.validMaskedPasswordKeysIndexes, webClientStub.validPassword).build();
        if (!jsonObject.getJsonObject("maskedPassword").equals(maskedPasswordJson))
            return false;
        if (jsonObject.getInt("avatarId") != webClientStub.validAvatarId)
            return false;
        if (!jsonObject.getString("loginScopeType").equals("WWW"))
            return false;
        return true;
    }
}