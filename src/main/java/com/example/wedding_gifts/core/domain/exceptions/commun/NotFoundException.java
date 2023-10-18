package com.example.wedding_gifts.core.domain.exceptions.commun;

public abstract class NotFoundException extends MyException {

    private static int statusCode = 422;

    public NotFoundException(String message, String exception, Throwable cause) {
        super(cause, statusCode, exception, message);
    }

    public NotFoundException(String message, String exception) {
        super(statusCode, exception, message);
    }

    public NotFoundException(String exception) {
        super(statusCode, exception, "Not Found");
    }

    public NotFoundException() {
        super(statusCode, "NotFoundException.class", "Not Found");
    }

}
