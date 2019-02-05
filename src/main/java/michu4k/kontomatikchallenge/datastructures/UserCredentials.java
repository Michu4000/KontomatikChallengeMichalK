package michu4k.kontomatikchallenge.datastructures;

public class UserCredentials {
    private String login;
    private String password;
    private int avatarId;

    public UserCredentials() {
        this.avatarId = -1;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(int avatarId) {
        this.avatarId = avatarId;
    }

    public boolean isEverythingFilled() {
        return (login.length() > 0 && password.length() > 0 && avatarId > -1);
    }

    public int getPasswordLength() {
        return password.length();
    }
}
