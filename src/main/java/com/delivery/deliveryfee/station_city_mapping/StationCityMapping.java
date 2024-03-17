package com.delivery.deliveryfee.station_city_mapping;

import jakarta.persistence.*;

@Entity
@Table(name = "station_city_mapping")
public class StationCityMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "city_name", unique = true)
    private String cityName;
    @Column(name = "station_name", unique = true)
    private String stationName;

    public StationCityMapping() {
    }

    public StationCityMapping(String cityName, String stationName) {
        this.cityName = cityName;
        this.stationName = stationName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }
}
