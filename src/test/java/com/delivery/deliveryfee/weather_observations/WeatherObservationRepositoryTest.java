package com.delivery.deliveryfee.weather_observations;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@DataJpaTest
public class WeatherObservationRepositoryTest {

    @Autowired
    private WeatherObservationRepository weatherObservationRepository;

    @BeforeEach
    public void setUp() {
        LocalDateTime now = LocalDateTime.now();

        WeatherObservation wo1 = new WeatherObservation(
                "Tallinn-Harku", "26038", 1.3,
                1.4,"", now
        );
        WeatherObservation wo2 = new WeatherObservation(
                "Tartu-Tõravere", "26242", 0.9,
                2.1,"", now
        );
        WeatherObservation wo3 = new WeatherObservation(
                "Pärnu", "41803", 1.3,
                0.8,"", now
        );
        WeatherObservation wo4 = new WeatherObservation(
                "Tallinn-Harku", "26038", 2.1,
                1.5,"", now.minusHours(1)
        );
        WeatherObservation wo5 = new WeatherObservation(
                "Tallinn-Harku", "26038", 3.1,
                3.0,"", now.minusDays(1)
        );

        List<WeatherObservation> weatherObservations = new ArrayList<>(
                List.of(wo1, wo2, wo3, wo4, wo5));
        System.out.println(List.of(weatherObservations));
        weatherObservationRepository.saveAll(weatherObservations);
    }

    @Test
    void testGetAllWeatherObservationsCount(){
        assert (weatherObservationRepository.findAll().size() == 4);
    }

    @Test
    void testGetWeatherObservationByStationName(){
        String station = "Tallinn-Harku";
        Optional<WeatherObservation> weatherObservation = weatherObservationRepository
                .findTopByStationNameOrderByTimeOfObservationDesc(station);
        assert weatherObservation.isPresent();
        assert (weatherObservation.get().getStationName().equals(station));
        assert (weatherObservation.get().getAirTemperature() == 2.1);
        assert (weatherObservation.get().getWindSpeed() == 1.5);
    }

    @Test
    void testWeatherObservationByCityNameAndTimestamp(){
        LocalDateTime dateTime = LocalDateTime.now();
        String station = "Tartu";

        Optional<WeatherObservation> weatherObservation = weatherObservationRepository
                .findTopByStationNameAndTimeOfObservationIsBeforeOrderByTimeOfObservationDesc(station, dateTime);

        assert weatherObservation.isPresent();
        //assert (weatherObservation.get().getAirTemperature());

    }

}
