package com.delivery.deliveryfee.delivery_fees;

import com.delivery.deliveryfee.enums.VehicleType;
import com.delivery.deliveryfee.weather_observations.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@DataJpaTest
public class DeliveryFeeCalculationServiceTest {

    @Autowired
    private RegionalBaseFeeRepository regionalBaseFeeRepository;

    @Autowired
    private WeatherObservationRepository weatherObservationRepository;

    @Mock
    private WeatherObservationService weatherObservationService;

    @Autowired
    private DeliveryFeeCalculationService deliveryFeeCalculationService;

    private WeatherObservationDTOMapper mapper;

    @BeforeEach
    public void setUp() {
        LocalDateTime now = LocalDateTime.now();
        List<WeatherObservation> weatherObservations = getWeatherObservations(now);
        WeatherObservation woTarget = new WeatherObservation(
                "Tartu-Tõravere", "26242", 1.4,
                0.5,"", now.plusHours(1)
        );
        List<RegionalBaseFee> regionalBaseFees = getRegionalBaseFees();

        weatherObservationRepository.saveAll(weatherObservations);
        regionalBaseFeeRepository.saveAll(regionalBaseFees);

        MockitoAnnotations.initMocks(this);
        when(weatherObservationService.getLatestObservationByCityName(anyString()))
                .thenReturn(mapper.apply(woTarget));




    }

    private static List<WeatherObservation> getWeatherObservations(LocalDateTime now) {
        WeatherObservation wo1 = new WeatherObservation(
                "Tallinn-Harku", "26038", 1.3,
                1.4,"", now
        );
        WeatherObservation wo2 = new WeatherObservation(
                "Tartu-Tõravere", "26242", 0.9,
                2.1,"", now
        );

        return new ArrayList<>(List.of(wo1, wo2));
    }

    private static List<RegionalBaseFee> getRegionalBaseFees(){

        RegionalBaseFee regionalBaseFee1 = new RegionalBaseFee(
                "Tallinn", VehicleType.SCOOTER, 3.0
        );

        RegionalBaseFee regionalBaseFee2 = new RegionalBaseFee(
                "Tartu", VehicleType.SCOOTER, 2.5
        );

        RegionalBaseFee regionalBaseFee3= new RegionalBaseFee(
                "Tartu", VehicleType.BIKE, 2.0
        );

        return new ArrayList<>(
                List.of(regionalBaseFee1, regionalBaseFee2, regionalBaseFee3));
    }
}
