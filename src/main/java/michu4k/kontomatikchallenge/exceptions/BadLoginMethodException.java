package michu4k.kontomatikchallenge.exceptions;

public class BadLoginMethodException extends RuntimeException {
    public BadLoginMethodException(){}

    public BadLoginMethodException(Throwable throwable) {
        super(throwable);
    }
}