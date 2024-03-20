package com.delivery.deliveryfee.exceptions;

public class NoUsageAllowedException extends IllegalArgumentException{
    private final String message;

    public NoUsageAllowedException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
