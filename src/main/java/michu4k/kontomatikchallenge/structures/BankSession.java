package michu4k.kontomatikchallenge.structures;

import java.util.Objects;

public class BankSession {
    public String sessionToken;
    public int userId;

    public BankSession() {
        this.userId = -1;
    }

    public boolean isSessionBlank() {
        return (sessionToken == null || sessionToken.length() == 0 || userId == -1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionToken, userId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof BankSession))
            return false;

        BankSession bankSessionObj = (BankSession) obj;
        boolean isUserIdEqual = bankSessionObj.userId == this.userId;
        boolean isSessionTokenEqual = bankSessionObj.sessionToken.equals(this.sessionToken);
        return (isUserIdEqual && isSessionTokenEqual);
    }
}