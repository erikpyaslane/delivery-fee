package com.delivery.deliveryfee.weather_observations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherObservationRepository extends JpaRepository<WeatherObservation, Long> {

    List<WeatherObservation> findWeatherObservationsByStationName(String stationName);

    Optional<WeatherObservation> findTopByStationNameOrderByTimeOfObservationDesc(String stationName);

    List<WeatherObservation> findTop3ByOrderByTimeOfObservationDesc();
}
