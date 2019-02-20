package michu4k.kontomatikchallenge.structures;

public class UserCredentials {
    public String login;
    public String password;
    public int avatarId;

    public UserCredentials() {
        this.avatarId = -1;
    }

    public boolean isEverythingFilled() {
        return (login.length() > 0 && password.length() > 0 && avatarId > -1);
    }
}