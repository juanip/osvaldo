package ova.exceptions;

public class OvaException extends RuntimeException {
    private static final long serialVersionUID = -4558831231531164603L;

    public OvaException(String message) {
        super(message);
    }

    public OvaException(Exception e) {
        super(e);
    }
}
