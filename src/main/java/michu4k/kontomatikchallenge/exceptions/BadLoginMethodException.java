package michu4k.kontomatikchallenge.exceptions;

public class BadLoginMethodException extends RuntimeException {
    public BadLoginMethodException(String message){
        super(message);
    }
}