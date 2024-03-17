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

    @GetMapping
    public List<WeatherObservationDTO> getAllWeatherObservations() {
        return weatherObservationService.getAllWeatherObservations();
    }

    @GetMapping("/latest")
    public List<WeatherObservationDTO> getLatestWeatherObservations() {
        return weatherObservationService.getLatestWeatherObservations();
    }

    @GetMapping("/{cityName}")
    public List<WeatherObservationDTO> getWeatherObservationsByCityName(@PathVariable String cityName) {
        System.out.println(cityName);
        return weatherObservationService.getObservationsByCityName(cityName);
    }

    @GetMapping("/{cityName}/latest")
    public WeatherObservationDTO getWeatherObservationByCityName(@PathVariable String cityName) {
        System.out.println(cityName);
        return weatherObservationService.getLatestObservationByCityName(cityName);
    }

    @GetMapping("/update")
    public String update() {
        weatherObservationService.updateWeatherData();
        return "Weather updated!";
    }
}
