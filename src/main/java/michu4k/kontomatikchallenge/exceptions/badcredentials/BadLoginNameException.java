package michu4k.kontomatikchallenge.exceptions.badcredentials;

public class BadLoginNameException extends BadCredentialsException {
    public BadLoginNameException(String message, Throwable throwable) {
        super(message, throwable);
    }
}