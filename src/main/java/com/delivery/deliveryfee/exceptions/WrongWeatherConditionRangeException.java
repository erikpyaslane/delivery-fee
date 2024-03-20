package com.delivery.deliveryfee.exceptions;

public class WrongWeatherConditionRangeException extends IllegalArgumentException{
    private final String message;

    public WrongWeatherConditionRangeException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
