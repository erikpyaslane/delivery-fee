package com.delivery.deliveryfee.exceptions;


public class WeatherObservationNotFoundException extends RuntimeException {

    private final String message;
    public WeatherObservationNotFoundException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
