package com.delivery.deliveryfee.station_city_mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StationCityMappingService {

    private final StationCityMappingRepository stationCityMappingRepository;

    @Autowired
    public StationCityMappingService(StationCityMappingRepository stationCityMappingRepository) {
        this.stationCityMappingRepository = stationCityMappingRepository;
    }


    /**
     * Gets station name by city name
     *
     * @param cityName name of city
     * @return station name
     */
    public String getStationNameByCityName(String cityName) {
        return stationCityMappingRepository.findStationNameByCityName(cityName);
    }

    /**
     * Gets city name by station name
     *
     * @param stationName name of station
     * @return city name
     */
    public String getCityNameByStationName(String stationName) {
        return stationCityMappingRepository.findStationNameByCityName(stationName);
    }

    /**
     * Returns true if station exist in db table, otherwise false
     *
     * @param stationName name of station
     * @return true/false depending on existence of station
     */
    public boolean existsStationName(String stationName) {
        return stationCityMappingRepository.existsByStationName(stationName);
    }

}
