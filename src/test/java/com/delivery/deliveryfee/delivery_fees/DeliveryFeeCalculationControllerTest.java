package com.delivery.deliveryfee.delivery_fees;

import com.delivery.deliveryfee.enums.VehicleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class DeliveryFeeCalculationControllerTest {
    @Mock
    private DeliveryFeeCalculationService deliveryFeeCalculationService;

    @InjectMocks
    private DeliveryFeeCalculationController deliveryFeeCalculationController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetDeliveryFeeWithTimestamp_Success() {
        String cityName = "Tartu";
        String vehicleType = "SCOOTER";
        String localDateTime = LocalDateTime.now().toString();
        ResponseEntity<String> expectedResponse = new ResponseEntity<>(
                "Delivery fee : 3.0", HttpStatus.OK);

        when(deliveryFeeCalculationService.getCalculationFee(
                eq(cityName), eq(vehicleType), eq(localDateTime)))
                .thenReturn(expectedResponse);

        ResponseEntity<String> actualResponse = deliveryFeeCalculationController
                .getDeliveryFee(cityName, vehicleType, localDateTime);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testGetDeliveryFeeWithoutTimestamp_Success() {
        String cityName = "Tartu";
        String vehicleType = "SCOOTER";
        ResponseEntity<String> expectedResponse = new ResponseEntity<>(
                "Delivery fee : 3.0", HttpStatus.OK);

        when(deliveryFeeCalculationService.getCalculationFee(
                eq(cityName), eq(vehicleType), eq("")))
                .thenReturn(expectedResponse);

        ResponseEntity<String> actualResponse = deliveryFeeCalculationController
                .getDeliveryFee(cityName, vehicleType, "");

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testGetDeliveryFee_InvalidCityName1() {
        String cityName = "";
        String vehicleType = "CAR";
        String localDateTime = "2024-03-21T12:00:00";
        ResponseEntity<String> expectedResponse = new ResponseEntity<>(
                "Invalid city input!", HttpStatus.BAD_REQUEST);

        when(deliveryFeeCalculationService.getCalculationFee(
                eq(cityName), eq(vehicleType), eq(localDateTime)))
                .thenReturn(expectedResponse);

        ResponseEntity<String> actualResponse = deliveryFeeCalculationController
                .getDeliveryFee(cityName, vehicleType, localDateTime);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testGetDeliveryFee_InvalidCityName2() {
        String cityName = "Tar1Tu";
        String vehicleType = "CAR";
        ResponseEntity<String> expectedResponse = new ResponseEntity<>(
                "Invalid city input!", HttpStatus.BAD_REQUEST);

        when(deliveryFeeCalculationService.getCalculationFee(
                eq(cityName), eq(vehicleType), eq("")))
                .thenReturn(expectedResponse);

        ResponseEntity<String> actualResponse = deliveryFeeCalculationController
                .getDeliveryFee(cityName, vehicleType, "");

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testGetDeliveryFee_InvalidVehicleType1() {
        String cityName = "Tartu";
        String vehicleType = "CARISCAR";
        ResponseEntity<String> expectedResponse = new ResponseEntity<>(
                "No such vehicle type exist", HttpStatus.BAD_REQUEST);

        when(deliveryFeeCalculationService.getCalculationFee(
                eq(cityName), eq(vehicleType), eq("")))
                .thenReturn(expectedResponse);

        ResponseEntity<String> actualResponse = deliveryFeeCalculationController
                .getDeliveryFee(cityName, vehicleType, "");

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testGetDeliveryFee_InvalidVehicleType2() {
        String cityName = "Tartu";
        String vehicleType = "";
        ResponseEntity<String> expectedResponse = new ResponseEntity<>(
                "No such vehicle type exist", HttpStatus.BAD_REQUEST);

        when(deliveryFeeCalculationService.getCalculationFee(
                eq(cityName), eq(vehicleType), eq("")))
                .thenReturn(expectedResponse);

        ResponseEntity<String> actualResponse = deliveryFeeCalculationController
                .getDeliveryFee(cityName, vehicleType, "");

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testGetRegionalBaseFees_Success() {
        List<RegionalBaseFee> expectedBaseFees = Collections.singletonList(new RegionalBaseFee());

        when(deliveryFeeCalculationService.getRegionalBaseFees()).thenReturn(expectedBaseFees);

        List<RegionalBaseFee> actualBaseFees = deliveryFeeCalculationController.getRegionalBaseFees();

        assertEquals(expectedBaseFees, actualBaseFees);
    }

    @Test
    public void testUpdateRegionalBaseFee_Success() {
        long id = 1;
        RegionalBaseFeeDTOWithoutId regionalBaseFeeDTO = new RegionalBaseFeeDTOWithoutId(
                "Tartu", VehicleType.SCOOTER, 3.0
        );
        ResponseEntity<String> expectedResponse = new ResponseEntity<>(
                "Regional Base Fee was successfully updated!", HttpStatus.OK
        );

        when(deliveryFeeCalculationService.updateRegionalBaseFee(
                eq(id), eq(regionalBaseFeeDTO)))
                .thenReturn(expectedResponse);

        ResponseEntity<String> actualResponse = deliveryFeeCalculationController
                .updateRegionalBaseFeeOfCity(id, regionalBaseFeeDTO);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testUpdateRegionalBaseFee_InvalidBaseFeeValue() {
        long id = 1;
        RegionalBaseFeeDTOWithoutId regionalBaseFeeDTO = new RegionalBaseFeeDTOWithoutId(
                "Tartu", VehicleType.SCOOTER, -50.0);
        ResponseEntity<String> expectedResponse = new ResponseEntity<>(
                "Base fee can't be negative", HttpStatus.BAD_REQUEST);

        when(deliveryFeeCalculationService.updateRegionalBaseFee(
                eq(id), eq(regionalBaseFeeDTO)))
                .thenReturn(expectedResponse);

        ResponseEntity<String> actualResponse = deliveryFeeCalculationController
                .updateRegionalBaseFeeOfCity(id, regionalBaseFeeDTO);

        assertEquals(expectedResponse, actualResponse);
    }

}
