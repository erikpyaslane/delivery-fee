package com.delivery.deliveryfee.business_rules;

import com.delivery.deliveryfee.enums.PhenomenonType;
import com.delivery.deliveryfee.enums.VehicleType;
import com.delivery.deliveryfee.enums.WeatherConditionType;
import com.delivery.deliveryfee.exceptions.WrongWeatherConditionRangeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BusinessRuleServiceTest {

    @Mock
    private BusinessRuleRepository businessRuleRepository;

    @Mock
    private BusinessRuleDTOMapper businessRuleDTOMapper;

    @InjectMocks
    private BusinessRuleService businessRuleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetAllBusinessRules() {
        // Arrange
        List<BusinessRule> businessRules = new ArrayList<>();
        businessRules.add(new BusinessRule(VehicleType.CAR, -10.0, 0.0,
                1.0, WeatherConditionType.ATEF, PhenomenonType.SNOW));
        when(businessRuleRepository.findAll()).thenReturn(businessRules);

        List<BusinessRuleDTO> expectedDTOs = new ArrayList<>();
        expectedDTOs.add(new BusinessRuleDTO(1L, VehicleType.CAR, -10.0, 0.0,
                1.0, WeatherConditionType.ATEF, PhenomenonType.SNOW));
        when(businessRuleDTOMapper.BusinessRuleToDTO(any())).thenReturn(expectedDTOs.getFirst());

        // Act
        List<BusinessRuleDTO> result = businessRuleService.getAllBusinessRules();

        // Assert
        assertEquals(expectedDTOs, result);
    }

    @Test
    void testGetBusinessRuleByVehicleTypeAndPhenomenonType() {
        // Arrange
        VehicleType vehicleType = VehicleType.CAR;
        PhenomenonType phenomenonType = PhenomenonType.SNOW;
        BusinessRule businessRule = new BusinessRule(vehicleType, -10.0, 0.0,
                1.0, WeatherConditionType.ATEF, phenomenonType);
        when(businessRuleRepository.findBusinessRuleByVehicleTypeAndPhenomenonType(
                vehicleType, phenomenonType)).thenReturn(Optional.of(businessRule));

        BusinessRuleDTO expectedDTO = new BusinessRuleDTO(1L, vehicleType, -10.0, 0.0,
                1.0, WeatherConditionType.ATEF, phenomenonType);
        when(businessRuleDTOMapper.BusinessRuleToDTO(businessRule)).thenReturn(expectedDTO);

        // Act
        BusinessRuleDTO result = businessRuleService.getBusinessRuleByVehicleTypeAndPhenomenonType(
                vehicleType, phenomenonType);

        // Assert
        assertEquals(expectedDTO, result);
    }

    @Test
    void testGetBusinessRuleByVehicleTypeAndWeatherConditionTypeAndRangeValue() {
        // Arrange
        VehicleType vehicleType = VehicleType.CAR;
        WeatherConditionType weatherConditionType = WeatherConditionType.ATEF;
        double rangeValue = -10.0;
        BusinessRule businessRule = new BusinessRule(vehicleType, -10.0, 0.0,
                1.0, weatherConditionType, PhenomenonType.SNOW);
        when(businessRuleRepository.findBusinessRuleByVehicleTypeAndWeatherConditionTypeAndRangeValue(
                vehicleType, weatherConditionType, rangeValue))
                .thenReturn(Optional.of(businessRule));

        BusinessRuleDTO expectedDTO = new BusinessRuleDTO(1L, vehicleType, -10.0, 0.0,
                1.0, weatherConditionType, PhenomenonType.SNOW);
        when(businessRuleDTOMapper.BusinessRuleToDTO(businessRule)).thenReturn(expectedDTO);

        // Act
        BusinessRuleDTO result = businessRuleService.getBusinessRuleByVehicleTypeAndWeatherConditionTypeAndRangeValue(
                vehicleType, weatherConditionType, rangeValue);

        // Assert
        assertEquals(expectedDTO, result);
    }
}