package com.delivery.deliveryfee.station_city_mapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StationCityMappingRepository extends JpaRepository<StationCityMapping, Long> {

    @Query("SELECT br.stationName FROM StationCityMapping br WHERE br.cityName=:cityName")
    String findStationNameByCityName(String cityName);

    @Query("SELECT br.cityName FROM StationCityMapping br WHERE br.stationName=:stationName")
    String findCityNameByStationName(String stationName);

    @Query("SELECT CASE WHEN EXISTS (SELECT 1 FROM StationCityMapping scm WHERE" +
            " scm.stationName = :stationName) THEN TRUE ELSE FALSE END")
    boolean existsByStationName(String stationName);

    @Query("SELECT CASE WHEN EXISTS (SELECT 1 FROM StationCityMapping scm WHERE" +
            " scm.cityName = :cityName) THEN TRUE ELSE FALSE END")
    boolean existsByCityName(String cityName);

}
