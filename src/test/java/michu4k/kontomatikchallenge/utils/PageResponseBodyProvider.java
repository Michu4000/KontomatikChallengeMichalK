package michu4k.kontomatikchallenge.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class PageResponseBodyProvider {
    private final static String jsonResourcesDir = System.getProperty("user.dir") + "/src/test/resources/jsonresponses/";

    static String getBadLogin() throws IOException {
        return getStringFromTextFile("bad_login.txt");
    }

    static String getValidLoginBeginning() throws IOException {
        return getStringFromTextFile("valid_login_beginning.txt");
    }

    static String getValidLoginEndForNotMaskedPassword() throws IOException {
        return getStringFromTextFile("valid_login_end_for_not_masked_password.txt");
    }

    static String getBadPasswordAndAvatar() throws IOException {
        return getStringFromTextFile("bad_password_and_avatar.txt");
    }

    static String getValidPasswordAndAvatarBeginning() throws IOException {
        return getStringFromTextFile("valid_password_and_avatar_beginning.txt");
    }

    static String getValidPasswordAndAvatarEnd() throws IOException {
        return getStringFromTextFile("valid_password_and_avatar_end.txt");
    }

    static String getValidBankAccounts() throws IOException {
        return getStringFromTextFile("valid_bank_accounts.txt");
    }

    static String getBadSessionToken() throws IOException {
        return getStringFromTextFile("bad_session_token.txt");
    }

    private static String getStringFromTextFile(String fileName) throws IOException {
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(jsonResourcesDir + fileName))) {
            return bufferedReader.readLine();
        }
    }
}