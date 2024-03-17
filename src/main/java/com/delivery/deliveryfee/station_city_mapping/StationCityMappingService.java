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


    public String getStationNameByCityName(String cityName) {
        return stationCityMappingRepository.findStationNameByCityName(cityName);
    }

    public String getCityNameByStationName(String cityName) {
        return stationCityMappingRepository.findStationNameByCityName(cityName);
    }

    public boolean existsStationName(String stationName) {
        return stationCityMappingRepository.existsByStationName(stationName);
    }

}
