package michu4k.kontomatikchallenge.datastructures;

public class BankSession {
    public String sessionToken;
    public int userId;

    public BankSession() {
        this.userId = -1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (!(obj instanceof BankSession))
            return false;

        boolean isUserIdEqual = ((BankSession) obj).userId == this.userId;
        boolean isSessionTokenEqual = ((BankSession) obj).sessionToken.equals(this.sessionToken);

        if (isUserIdEqual && isSessionTokenEqual)
            return true;
        else
            return false;
    }
}
