package michu4k.kontomatikchallenge.exceptions.badcredentials;

public class BadCredentialsException extends RuntimeException {
    public BadCredentialsException(Throwable throwable) {
        super(throwable);
    }

    BadCredentialsException(String message) {
        super(message);
    }
}