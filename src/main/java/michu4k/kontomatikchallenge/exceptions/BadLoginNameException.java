package michu4k.kontomatikchallenge.exceptions;

public class BadLoginNameException extends BadCredentialsException {
    public BadLoginNameException(Throwable throwable) {
        super(throwable);
    }
}