package michu4k.kontomatikchallenge.exceptions;

public class BadLoginException extends BadCredentialsException {
    public BadLoginException(){}

    public BadLoginException(Throwable throwable) {
        super(throwable);
    }
}
