package com.delivery.deliveryfee.business_rules;

import org.springframework.stereotype.Component;

@Component
public class BusinessRuleDTOMapper {

    public BusinessRuleDTO BusinessRuleToDTO(BusinessRule businessRule) {
        return new BusinessRuleDTO(
                businessRule.getId(),
                businessRule.getVehicleType(),
                businessRule.getMinValueOfRange(),
                businessRule.getMaxValueOfRange(),
                businessRule.getExtraFeeValue(),
                businessRule.getWeatherConditionType(),
                businessRule.getPhenomenonType()
        );
    }

    public BusinessRule DTOToBusinessRule(BusinessRuleDTOWithoutId businessRuleDTO) {
        return new BusinessRule(
                businessRuleDTO.vehicleType(),
                businessRuleDTO.minValueOfRange(),
                businessRuleDTO.maxValueOfRange(),
                businessRuleDTO.extraFeeValue(),
                businessRuleDTO.weatherConditionType(),
                businessRuleDTO.phenomenonType()
        );
    }
}
