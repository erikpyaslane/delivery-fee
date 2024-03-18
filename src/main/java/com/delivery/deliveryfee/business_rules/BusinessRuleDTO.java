package com.delivery.deliveryfee.business_rules;

import com.delivery.deliveryfee.enums.PhenomenonType;
import com.delivery.deliveryfee.enums.VehicleType;
import com.delivery.deliveryfee.enums.WeatherConditionType;

public record BusinessRuleDTO(
        Long id,
        VehicleType vehicleType,
        Double minValueOfRange,
        Double maxValueOfRange,
        Double extraFeeValue,
        WeatherConditionType weatherConditionType,
        PhenomenonType phenomenonType
) {

}
