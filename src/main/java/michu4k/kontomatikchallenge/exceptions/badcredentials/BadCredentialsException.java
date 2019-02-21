package michu4k.kontomatikchallenge.exceptions.badcredentials;

public class BadCredentialsException extends RuntimeException {
    public BadCredentialsException(String message, Throwable throwable) {
        super(message, throwable);
    }

    BadCredentialsException(String message) {
        super(message);
    }
}