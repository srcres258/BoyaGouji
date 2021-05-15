package com.thws.boyaddz;

public class IllegalGJCardsException extends RuntimeException {
    public IllegalGJCardsException() {
    }

    public IllegalGJCardsException(String message) {
        super(message);
    }

    public IllegalGJCardsException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalGJCardsException(Throwable cause) {
        super(cause);
    }

    public IllegalGJCardsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
