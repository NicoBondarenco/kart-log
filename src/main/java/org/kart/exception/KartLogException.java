package org.kart.exception;

public class KartLogException extends RuntimeException {

    public KartLogException(String message) {
        super(message);
    }

    public KartLogException(String message, Throwable cause) {
        super(message, cause);
    }

}
