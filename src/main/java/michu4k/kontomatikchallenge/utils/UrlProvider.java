package michu4k.kontomatikchallenge.utils;

public class UrlProvider {
    private static final String DOMAIN_URL = "https://login.nestbank.pl/";

    public final static String LOGIN_SITE_URL = DOMAIN_URL + "rest/v1/auth/checkLogin";
    public final static String PASSWORD_AND_AVATAR_SITE_URL = DOMAIN_URL + "rest/v1/auth/loginByPartialPassword";

    public final static String BANK_ACCOUNTS_SITE_URL_BEGINNING = DOMAIN_URL + "rest/v1/context/";
    public final static String BANK_ACCOUNTS_SITE_URL_END = "/dashboard/www/config";

    public final static String REQUEST_POST_REFERER_URL = DOMAIN_URL + "login";
    public final static String REQUEST_GET_REFERER_URL = DOMAIN_URL + "dashboard/products";
}