package michu4k.kontomatikchallenge.exceptions;

public class BadPasswordException extends BadCredentialsException {
    public BadPasswordException(){}

    public BadPasswordException(Throwable throwable) {
        super(throwable);
    }
}
