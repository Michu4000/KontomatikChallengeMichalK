package michu4k.kontomatikchallenge.exceptions;

public class BadCredentialsException extends RuntimeException {
    public BadCredentialsException(){}

    public BadCredentialsException(Throwable throwable) {
        super(throwable);
    }
}