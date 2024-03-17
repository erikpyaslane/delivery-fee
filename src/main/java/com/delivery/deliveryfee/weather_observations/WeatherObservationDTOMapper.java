package com.delivery.deliveryfee.weather_observations;

import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class WeatherObservationDTOMapper implements Function<WeatherObservation, WeatherObservationDTO> {

    @Override
    public WeatherObservationDTO apply(WeatherObservation weatherObservation) {
        return new WeatherObservationDTO(
                weatherObservation.getId(),
                weatherObservation.getStationName(),
                weatherObservation.getStationCodeWMO(),
                weatherObservation.getAirTemperature(),
                weatherObservation.getWindSpeed(),
                weatherObservation.getWeatherPhenomenon(),
                weatherObservation.getTimeOfObservation());
    }
}
