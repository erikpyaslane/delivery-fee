package com.delivery.deliveryfee.delivery_fees;

import com.delivery.deliveryfee.enums.VehicleType;
import com.delivery.deliveryfee.weather_observations.WeatherObservation;
import com.delivery.deliveryfee.weather_observations.WeatherObservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.annotation.AccessType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@DataJpaTest
public class DeliveryFeeRepositoryTest {

    @Autowired
    private RegionalBaseFeeRepository regionalBaseFeeRepository;

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

        RegionalBaseFee regionalBaseFee1 = new RegionalBaseFee(
                "Tallinn", VehicleType.SCOOTER, 3.5
        );

        RegionalBaseFee regionalBaseFee2 = new RegionalBaseFee(
                "Tartu", VehicleType.SCOOTER, 3.0
        );

        RegionalBaseFee regionalBaseFee3= new RegionalBaseFee(
                "Tartu", VehicleType.BIKE, 2.5
        );

        List<WeatherObservation> weatherObservations = new ArrayList<>(List.of(wo1, wo2));
        List<RegionalBaseFee> regionalBaseFees = new ArrayList<>(
                List.of(regionalBaseFee1, regionalBaseFee2, regionalBaseFee3));

        weatherObservationRepository.saveAll(weatherObservations);
        regionalBaseFeeRepository.saveAll(regionalBaseFees);
    }

    @Test
    void testGetRegionalBaseFeeByCityNameAndVehicleType(){
        String city = "Tartu";
        VehicleType vehicleType = VehicleType.SCOOTER;

        RegionalBaseFee regionalBaseFee = regionalBaseFeeRepository.findTopByCityNameAndVehicleType(
                city, vehicleType
        );

        assert (regionalBaseFee.getCityName().equals(city));
        assert (regionalBaseFee.getVehicleType() == vehicleType);
        assert (regionalBaseFee.getBaseFeeValue() == 2.5);

    }

    @Test
    void testGetRegionalBaseFeeByCityNameThatDoNotExist(){
        String city = "Tart";
        VehicleType vehicleType = VehicleType.SCOOTER;

        RegionalBaseFee regionalBaseFee = regionalBaseFeeRepository.findTopByCityNameAndVehicleType(
                city, vehicleType
        );

        assert (regionalBaseFee == null);

    }

}
