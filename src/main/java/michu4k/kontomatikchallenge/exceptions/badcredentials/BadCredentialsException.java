package michu4k.kontomatikchallenge.exceptions.badcredentials;

public class BadCredentialsException extends RuntimeException {
    BadCredentialsException(){}

    public BadCredentialsException(Throwable throwable) {
        super(throwable);
    }
}