package com.delivery.deliveryfee.business_rules;

import com.delivery.deliveryfee.enums.PhenomenonType;
import com.delivery.deliveryfee.enums.VehicleType;
import com.delivery.deliveryfee.enums.WeatherConditionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class BusinessRuleControllerTest {

    @Mock
    private BusinessRuleService businessRuleService;

    @InjectMocks
    private BusinessRuleController businessRuleController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    void testGetAllBusinessRules() {
    // Arrange
    List<BusinessRuleDTO> dummyList = new ArrayList<>();
    when(businessRuleService.getAllBusinessRules()).thenReturn(dummyList);

    // Act
    List<BusinessRuleDTO> result = businessRuleController.getAllBusinessRules();

    // Assert
    assertEquals(dummyList, result);
}

@Test
void testCreateBusinessRule() {
    // Arrange
    BusinessRuleDTOWithoutId dtoWithoutId = new BusinessRuleDTOWithoutId(
            VehicleType.CAR, -10.0, 0.0, 1.0, WeatherConditionType.ATEF, PhenomenonType.SNOW
    );
    BusinessRuleDTO createdDto = new BusinessRuleDTO(
            1L, // Assuming the ID will be generated
            dtoWithoutId.vehicleType(),
            dtoWithoutId.minValueOfRange(),
            dtoWithoutId.maxValueOfRange(),
            dtoWithoutId.extraFeeValue(),
            dtoWithoutId.weatherConditionType(),
            dtoWithoutId.phenomenonType()
    );
    when(businessRuleService.saveBusinessRule(dtoWithoutId)).thenReturn(createdDto);

    // Act
    ResponseEntity<BusinessRuleDTO> responseEntity = businessRuleController.createBusinessRule(dtoWithoutId);

    // Assert
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    verify(businessRuleService, times(1)).saveBusinessRule(dtoWithoutId);
}

@Test
void testUpdateBusinessRule() {
    // Arrange
    long id = 1L;
    BusinessRuleDTOWithoutId dtoWithoutId = new BusinessRuleDTOWithoutId(
            VehicleType.CAR, -10.0, 0.0, 1.0, WeatherConditionType.ATEF, PhenomenonType.SNOW
    );

    BusinessRuleDTO updatedDto = new BusinessRuleDTO(
            id, // Assuming the ID will be generated
            dtoWithoutId.vehicleType(),
            dtoWithoutId.minValueOfRange(),
            dtoWithoutId.maxValueOfRange(),
            dtoWithoutId.extraFeeValue(),
            dtoWithoutId.weatherConditionType(),
            dtoWithoutId.phenomenonType()
    );
    when(businessRuleService.saveBusinessRule(dtoWithoutId)).thenReturn(updatedDto);

    when(businessRuleService.updateBusinessRule(id, dtoWithoutId)).thenReturn(updatedDto
    );

    // Act
    ResponseEntity<BusinessRuleDTO> responseEntity = businessRuleController.updateBusinessRule(id, dtoWithoutId);

    // Assert
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    verify(businessRuleService, times(1)).updateBusinessRule(id, dtoWithoutId);
}

@Test
void testDeleteBusinessRule() {
    // Arrange
    long id = 1L;
    when(businessRuleService.removeBusinessRule(id)).thenReturn(new ResponseEntity<>("Deleted successfully", HttpStatus.NO_CONTENT));

    // Act
    ResponseEntity<String> responseEntity = businessRuleController.deleteBusinessRule(id);

    // Assert
    assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    verify(businessRuleService, times(1)).removeBusinessRule(id);
}
}