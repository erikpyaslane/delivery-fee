package com.delivery.deliveryfee.weather_observations;

import java.time.LocalDateTime;

public record WeatherObservationDTO(
        Long id,
        String stationName,
        String wmoCode,
        double airTemperature,
        double windSpeed,
        String weatherPhenomenon,
        LocalDateTime timeOfObservation
) {

}
