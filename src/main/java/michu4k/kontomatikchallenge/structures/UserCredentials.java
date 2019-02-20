package michu4k.kontomatikchallenge.structures;

import java.util.Objects;

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

    @Override
    public int hashCode() {
        return Objects.hash(login, password, avatarId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof UserCredentials))
            return false;

        UserCredentials userCredentialsObj = (UserCredentials) obj;
        boolean isLoginEqual = userCredentialsObj.login.equals(this.login);
        boolean isPasswordEqual = userCredentialsObj.password.equals(this.password);
        boolean isAvatarIdEqual = userCredentialsObj.avatarId == this.avatarId;
        return (isLoginEqual && isPasswordEqual && isAvatarIdEqual);
    }
}