package Exceptions;

public class TooFewFieldsException extends Exception {
    public TooFewFieldsException(String message) {
        super(message);
    }
}