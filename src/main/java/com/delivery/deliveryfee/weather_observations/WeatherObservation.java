package com.delivery.deliveryfee.weather_observations;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "weather_observations")
public class WeatherObservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "station_name")
    @NotNull
    private String stationName;
    @Column(name = "wmo_code")
    @NotNull
    private String stationCodeWMO;
    @Column(name = "air_temperature")
    @NotNull
    private double airTemperature;
    @Column(name = "wind_speed")
    @NotNull
    private double windSpeed;
    @Column(name = "weather_phenomenon")
    private String weatherPhenomenon;
    @Column(name = "observation_timestamp")
    @NotNull
    private LocalDateTime timeOfObservation;

    public WeatherObservation() {
    }

    public WeatherObservation(String stationName, String stationCodeWMO, double airTemperature, double windSpeed, String weatherPhenomenon, LocalDateTime timeOfObservation) {
        this.stationName = stationName;
        this.stationCodeWMO = stationCodeWMO;
        this.airTemperature = airTemperature;
        this.windSpeed = windSpeed;
        this.weatherPhenomenon = weatherPhenomenon;
        this.timeOfObservation = timeOfObservation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getStationCodeWMO() {
        return stationCodeWMO;
    }

    public void setStationCodeWMO(String stationCodeWMO) {
        this.stationCodeWMO = stationCodeWMO;
    }

    public double getAirTemperature() {
        return airTemperature;
    }

    public void setAirTemperature(double airTemperature) {
        this.airTemperature = airTemperature;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getWeatherPhenomenon() {
        return weatherPhenomenon;
    }

    public void setWeatherPhenomenon(String weatherPhenomenon) {
        this.weatherPhenomenon = weatherPhenomenon;
    }

    public LocalDateTime getTimeOfObservation() {
        return timeOfObservation;
    }

    public void setTimeOfObservation(LocalDateTime timeOfObservation) {
        this.timeOfObservation = timeOfObservation;
    }

}
