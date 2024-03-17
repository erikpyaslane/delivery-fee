package com.delivery.deliveryfee.exceptions;

public class NoSuchVehicleTypeException extends IllegalArgumentException {

    private final String message;

    public NoSuchVehicleTypeException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
