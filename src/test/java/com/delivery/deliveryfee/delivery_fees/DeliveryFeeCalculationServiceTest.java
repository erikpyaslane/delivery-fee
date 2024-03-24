package com.delivery.deliveryfee.delivery_fees;

import com.delivery.deliveryfee.business_rules.*;
import com.delivery.deliveryfee.enums.PhenomenonType;
import com.delivery.deliveryfee.enums.VehicleType;
import com.delivery.deliveryfee.enums.WeatherConditionType;
import com.delivery.deliveryfee.station_city_mapping.StationCityMappingService;
import com.delivery.deliveryfee.weather_observations.WeatherObservationDTO;
import com.delivery.deliveryfee.weather_observations.WeatherObservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class DeliveryFeeCalculationServiceTest {

    @Mock
    private RegionalBaseFeeRepository regionalBaseFeeRepository;

    @Mock
    private RegionalBaseFeeDTOMapper regionalBaseFeeDTOMapper;

    @Mock
    private BusinessRuleDTOMapper businessRuleDTOMapper;

    @Mock
    private StationCityMappingService stationCityMappingService;

    @Mock
    private WeatherObservationService weatherObservationService;

    @Mock
    private BusinessRuleService businessRuleService;

    @InjectMocks
    private DeliveryFeeCalculationService deliveryFeeCalculationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void calculateDeliveryFeeWithoutAnyExtraRule() {
        String cityName = "Tartu";
        String vehicleType = VehicleType.BIKE.toString();
        RegionalBaseFee rgb = new RegionalBaseFee(cityName, VehicleType.valueOf(vehicleType),
                2.5);

        when(stationCityMappingService.existsCityName(cityName)).thenReturn(true);
        when(weatherObservationService.getLatestObservationByCityName(cityName)).thenReturn(
                        new WeatherObservationDTO(1L, "Tartu-Toravere", "24068",
                        5.1, 2.2, "",
                                LocalDateTime.now().minusHours(1)));
        when(regionalBaseFeeRepository
                .findTopByCityNameAndVehicleType(cityName, VehicleType.valueOf(vehicleType)))
                .thenReturn(rgb);
        when(businessRuleService
                .getBusinessRuleByVehicleTypeAndWeatherConditionTypeAndRangeValue(
                        VehicleType.valueOf(vehicleType),
                        WeatherConditionType.ATEF, 5.1))
                .thenReturn(null);
        when(businessRuleService
                .getBusinessRuleByVehicleTypeAndWeatherConditionTypeAndRangeValue(
                        VehicleType.valueOf(vehicleType),
                        WeatherConditionType.WSEF, 2.2))
                .thenReturn(null);
        when(businessRuleService
                .getBusinessRuleByVehicleTypeAndPhenomenonType(VehicleType.valueOf(vehicleType),
                        PhenomenonType.NOPE))
                .thenReturn(null);

        assertEquals(new ResponseEntity<>("Delivery fee : " + 2.5, HttpStatus.OK),
                deliveryFeeCalculationService.getCalculationFee(cityName, vehicleType, ""));
    }

    @Test
    void calculateDeliveryFeeWithATEFExtraRule() {
        String cityName = "Tartu";
        String vehicleType = VehicleType.BIKE.toString();
        RegionalBaseFee rgb = new RegionalBaseFee(cityName, VehicleType.valueOf(vehicleType),
                2.5);


        BusinessRuleDTO brDTO = new BusinessRuleDTO(1L, VehicleType.BIKE,
                -10.0, 0.0, 0.5, WeatherConditionType.ATEF,
                PhenomenonType.NOPE);

        when(stationCityMappingService.existsCityName(cityName)).thenReturn(true);
        when(weatherObservationService.getLatestObservationByCityName(cityName)).thenReturn(
                new WeatherObservationDTO(1L, "Tartu-Tõravere", "24068",
                        -1.1, 2.2, "",
                        LocalDateTime.now().minusHours(1)));
        when(regionalBaseFeeRepository
                .findTopByCityNameAndVehicleType(cityName, VehicleType.valueOf(vehicleType)))
                .thenReturn(rgb);
        when(businessRuleService
                .getBusinessRuleByVehicleTypeAndWeatherConditionTypeAndRangeValue(
                        VehicleType.valueOf(vehicleType),
                        WeatherConditionType.ATEF, -1.1))
                .thenReturn(brDTO);
        when(businessRuleService
                .getBusinessRuleByVehicleTypeAndWeatherConditionTypeAndRangeValue(
                        VehicleType.valueOf(vehicleType),
                        WeatherConditionType.WSEF, 2.2))
                .thenReturn(null);
        when(businessRuleService
                .getBusinessRuleByVehicleTypeAndPhenomenonType(VehicleType.valueOf(vehicleType),
                        PhenomenonType.NOPE))
                .thenReturn(null);

        assertEquals(new ResponseEntity<>("Delivery fee : " + 3.0, HttpStatus.OK),
                deliveryFeeCalculationService.getCalculationFee(cityName, vehicleType, ""));
    }

    @Test
    void calculateDeliveryFeeWithWSEFExtraRule() {
        String cityName = "Tartu";
        String vehicleType = VehicleType.BIKE.toString();
        RegionalBaseFee rgb = new RegionalBaseFee(cityName, VehicleType.valueOf(vehicleType),
                2.5);
        BusinessRuleDTO brDTO = new BusinessRuleDTO(1L, VehicleType.BIKE,
                10.0, 20.0, 0.5, WeatherConditionType.WSEF,
                PhenomenonType.NOPE);

        when(stationCityMappingService.existsCityName(cityName)).thenReturn(true);
        when(weatherObservationService.getLatestObservationByCityName(cityName)).thenReturn(
                new WeatherObservationDTO(1L, "Tartu-Toravere", "24068",
                        1.1, 10.2, "",
                        LocalDateTime.now().minusHours(1)));
        when(regionalBaseFeeRepository
                .findTopByCityNameAndVehicleType(cityName, VehicleType.valueOf(vehicleType)))
                .thenReturn(rgb);
        when(businessRuleService
                .getBusinessRuleByVehicleTypeAndWeatherConditionTypeAndRangeValue(
                        VehicleType.valueOf(vehicleType),
                        WeatherConditionType.ATEF, 1.1))
                .thenReturn(null);
        when(businessRuleService
                .getBusinessRuleByVehicleTypeAndWeatherConditionTypeAndRangeValue(
                        VehicleType.valueOf(vehicleType),
                        WeatherConditionType.WSEF, 10.2))
                .thenReturn(brDTO);
        when(businessRuleService
                .getBusinessRuleByVehicleTypeAndPhenomenonType(VehicleType.valueOf(vehicleType),
                        PhenomenonType.NOPE))
                .thenReturn(null);

        assertEquals(new ResponseEntity<>("Delivery fee : " + 3.0, HttpStatus.OK),
                deliveryFeeCalculationService.getCalculationFee(cityName, vehicleType, ""));
    }

    @Test
    void calculateDeliveryFeeWithWPEFExtraRule() {
        String cityName = "Tartu";
        String vehicleType = VehicleType.BIKE.toString();
        RegionalBaseFee rgb = new RegionalBaseFee(cityName, VehicleType.valueOf(vehicleType),
                2.5);
        BusinessRuleDTO brDTO = new BusinessRuleDTO(1L, VehicleType.BIKE,
                null, null, 0.5, WeatherConditionType.WSEF,
                PhenomenonType.RAIN);
        when(stationCityMappingService.existsCityName(cityName)).thenReturn(true);
        when(weatherObservationService.getLatestObservationByCityName(cityName)).thenReturn(
                new WeatherObservationDTO(1L, "Tartu-Toravere", "24068",
                        5.1, 2.2, "Light rain",
                        LocalDateTime.now().minusHours(1)));
        when(regionalBaseFeeRepository
                .findTopByCityNameAndVehicleType(cityName, VehicleType.valueOf(vehicleType)))
                .thenReturn(rgb);
        when(businessRuleService
                .getBusinessRuleByVehicleTypeAndWeatherConditionTypeAndRangeValue(
                        VehicleType.valueOf(vehicleType),
                        WeatherConditionType.ATEF, 5.1))
                .thenReturn(null);
        when(businessRuleService
                .getBusinessRuleByVehicleTypeAndWeatherConditionTypeAndRangeValue(
                        VehicleType.valueOf(vehicleType),
                        WeatherConditionType.WSEF, 2.2))
                .thenReturn(null);
        when(businessRuleService
                .getBusinessRuleByVehicleTypeAndPhenomenonType(VehicleType.valueOf(vehicleType),
                        PhenomenonType.RAIN))
                .thenReturn(brDTO);

        assertEquals(new ResponseEntity<>("Delivery fee : " + 3.0, HttpStatus.OK),
                deliveryFeeCalculationService.getCalculationFee(cityName, vehicleType, ""));
    }

    @Test
    void calculateDeliveryFeeWithAllExtraRules() {
        String cityName = "Tartu";
        String vehicleType = VehicleType.BIKE.toString();

        RegionalBaseFee rgb = new RegionalBaseFee(cityName, VehicleType.valueOf(vehicleType),
                2.5);

        BusinessRuleDTO brDTO1 = new BusinessRuleDTO(1L, VehicleType.BIKE,
                -10.0, 0.0, 0.5, WeatherConditionType.ATEF,
                PhenomenonType.NOPE);
        BusinessRuleDTO brDTO2 = new BusinessRuleDTO(2L, VehicleType.BIKE,
                10.0, 20.0, 0.5, WeatherConditionType.WSEF,
                PhenomenonType.NOPE);
        BusinessRuleDTO brDTO3 = new BusinessRuleDTO(3L, VehicleType.BIKE,
                null, null, 0.5, WeatherConditionType.WPEF,
                PhenomenonType.RAIN);

        when(stationCityMappingService.existsCityName(cityName)).thenReturn(true);
        when(weatherObservationService.getLatestObservationByCityName(cityName)).thenReturn(
                new WeatherObservationDTO(1L, "Tartu-Tõravere", "24068",
                        -1.1, 12.2, "Light rain",
                        LocalDateTime.now().minusHours(1)));
        when(regionalBaseFeeRepository
                .findTopByCityNameAndVehicleType(cityName, VehicleType.valueOf(vehicleType)))
                .thenReturn(rgb);
        when(businessRuleService
                .getBusinessRuleByVehicleTypeAndWeatherConditionTypeAndRangeValue(
                        VehicleType.valueOf(vehicleType),
                        WeatherConditionType.ATEF, -1.1))
                .thenReturn(brDTO1);
        when(businessRuleService
                .getBusinessRuleByVehicleTypeAndWeatherConditionTypeAndRangeValue(
                        VehicleType.valueOf(vehicleType),
                        WeatherConditionType.WSEF, 12.2))
                .thenReturn(brDTO2);
        when(businessRuleService
                .getBusinessRuleByVehicleTypeAndPhenomenonType(VehicleType.valueOf(vehicleType),
                        PhenomenonType.RAIN))
                .thenReturn(brDTO3);

        assertEquals(new ResponseEntity<>("Delivery fee : " + 4.0, HttpStatus.OK),
                deliveryFeeCalculationService.getCalculationFee(cityName, vehicleType, ""));
    }


}
