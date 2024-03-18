package com.delivery.deliveryfee.business_rules;

import com.delivery.deliveryfee.enums.PhenomenonType;
import com.delivery.deliveryfee.enums.VehicleType;
import com.delivery.deliveryfee.enums.WeatherConditionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BusinessRuleDTOWithoutId(

        @NotBlank VehicleType vehicleType,
        Double minValueOfRange,
        Double maxValueOfRange,
        @NotBlank Double extraFeeValue,
        @NotBlank WeatherConditionType weatherConditionType,
        @NotNull PhenomenonType phenomenonType
) {

}
