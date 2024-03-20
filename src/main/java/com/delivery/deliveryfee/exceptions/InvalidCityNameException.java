package com.delivery.deliveryfee.exceptions;

public class InvalidCityNameException extends IllegalArgumentException {

    private final String message;

    public InvalidCityNameException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
