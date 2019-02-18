package michu4k.kontomatikchallenge.utils;

public class UrlProvider {
    static final String DOMAIN_URL = "https://login.nestbank.pl/";

    public final static String LOGIN_SITE_URL = DOMAIN_URL + "rest/v1/auth/checkLogin";
    public final static String PASSWORD_AND_AVATAR_SITE_URL = DOMAIN_URL + "rest/v1/auth/loginByPartialPassword";

    public final static String BANK_ACCOUNTS_SITE_URL_BEGINNING = DOMAIN_URL + "rest/v1/context/";
    public final static String BANK_ACCOUNTS_SITE_URL_END = "/dashboard/www/config";
}