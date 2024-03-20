package com.delivery.deliveryfee.weather_observations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/weather_observations")
public class WeatherObservationController {

    private final WeatherObservationService weatherObservationService;

    @Autowired
    public WeatherObservationController(WeatherObservationService weatherObservationService) {
        this.weatherObservationService = weatherObservationService;
    }

    /**
     * Retrieves all weather observations
     *
     * @return list of weather observations
     */
    @GetMapping
    public List<WeatherObservationDTO> getAllWeatherObservations() {
        return weatherObservationService.getAllWeatherObservations();
    }
    /**
     * Retrieves latest weather observations
     *
     * @return list of weather observations
     */
    @GetMapping("/latest")
    public List<WeatherObservationDTO> getLatestWeatherObservations() {
        return weatherObservationService.getLatestWeatherObservations();
    }

    /**
     * Retrieves all weather observations of city
     *
     * @param cityName target city name
     * @return list of weather observations of target city
     */
    @GetMapping("/{cityName}")
    public List<WeatherObservationDTO> getWeatherObservationsByCityName(@PathVariable String cityName) {
        return weatherObservationService.getObservationsByCityName(cityName);
    }

    /**
     * Retrieves latest weather observation of city
     *
     * @return Weather observation Entity DTO
     */
    @GetMapping("/{cityName}/latest")
    public WeatherObservationDTO getWeatherObservationByCityName(@PathVariable String cityName) {
        System.out.println(cityName);
        return weatherObservationService.getLatestObservationByCityName(cityName);
    }
}
