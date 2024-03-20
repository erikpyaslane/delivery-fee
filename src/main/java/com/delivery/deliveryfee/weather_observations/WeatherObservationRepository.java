package com.delivery.deliveryfee.weather_observations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherObservationRepository extends JpaRepository<WeatherObservation, Long> {

    Optional<List<WeatherObservation>> findWeatherObservationsByStationName(String stationName);

    Optional<WeatherObservation> findTopByStationNameOrderByTimeOfObservationDesc(String stationName);

    Optional<List<WeatherObservation>> findTop3ByOrderByTimeOfObservationDesc();

    Optional<WeatherObservation> findTopByStationNameAndTimeOfObservationIsBeforeOrderByTimeOfObservationDesc(
            String stationName, LocalDateTime localDateTime
    );
}
