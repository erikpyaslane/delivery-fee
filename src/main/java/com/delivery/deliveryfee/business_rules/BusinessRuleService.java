package com.delivery.deliveryfee.business_rules;

import com.delivery.deliveryfee.enums.PhenomenonType;
import com.delivery.deliveryfee.enums.VehicleType;
import com.delivery.deliveryfee.enums.WeatherConditionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BusinessRuleService {

    private final BusinessRuleRepository businessRuleRepository;
    private final BusinessRuleDTOMapper businessRuleDTOMapper;

    @Autowired
    public BusinessRuleService(BusinessRuleRepository businessRuleRepository, BusinessRuleDTOMapper businessRuleDTOMapper) {
        this.businessRuleRepository = businessRuleRepository;
        this.businessRuleDTOMapper = businessRuleDTOMapper;
    }

    public List<BusinessRuleDTO> getAllBusinessRules() {
        List<BusinessRule> businessRules = businessRuleRepository.findAll();
        return businessRules.stream()
                .map(businessRuleDTOMapper::BusinessRuleToDTO)
                .collect(Collectors.toList());
    }


    public List<BusinessRuleDTO> getBusinessRulesByVehicleType(String vehicleType) {
        VehicleType convertedVehicleType = null;
        try {
            convertedVehicleType = VehicleType.valueOf(vehicleType);
        } catch (IllegalArgumentException e) {
            System.out.println("This vehicle type does not exist!");
        }
        List<BusinessRule> businessRules = businessRuleRepository.findBusinessRulesByVehicleType(convertedVehicleType);
        return businessRules.stream()
                .map(businessRuleDTOMapper::BusinessRuleToDTO)
                .collect(Collectors.toList());
    }

    public BusinessRuleDTO getBusinessRuleByVehicleTypeAndPhenomenonType(
            VehicleType vehicleType, PhenomenonType phenomenonType) {

        Optional<BusinessRule> optionalBusinessRule = businessRuleRepository
                .findBusinessRuleByVehicleTypeAndPhenomenonType(vehicleType, phenomenonType);

        return optionalBusinessRule.map(businessRuleDTOMapper::BusinessRuleToDTO).orElse(null);
    }

    public BusinessRuleDTO getBusinessRuleByVehicleTypeAndWeatherConditionTypeAndRangeValue(
            VehicleType vehicleType, WeatherConditionType weatherConditionType, double rangeValue) {
        Optional<BusinessRule> optionalBusinessRule = businessRuleRepository
                .findBusinessRuleByVehicleTypeAndWeatherConditionTypeAndRangeValue(
                vehicleType, weatherConditionType, rangeValue
        );

        return optionalBusinessRule.map(businessRuleDTOMapper::BusinessRuleToDTO).orElse(null);
    }

    public BusinessRuleDTO saveBusinessRule(BusinessRuleDTOWithoutId businessRuleDTOWithoutId) {
        BusinessRule businessRule = businessRuleDTOMapper.DTOToBusinessRule(businessRuleDTOWithoutId);
        List<BusinessRule> businessRules = businessRuleRepository
                .findBusinessRulesByVehicleTypeAndWeatherConditionType(businessRule.getVehicleType(), businessRule.getWeatherConditionType());
        if (!businessRuleRangesAreValid(businessRules, businessRule.getMinValueOfRange(), businessRule.getMaxValueOfRange()))
            return null;
        return businessRuleDTOMapper.BusinessRuleToDTO(
                businessRuleRepository.save(businessRule));
    }

    public BusinessRuleDTO updateBusinessRule(long id, BusinessRuleDTOWithoutId businessRuleDTOWithoutId) {
        Optional<BusinessRule> optionalBusinessRule = businessRuleRepository.findById(id);
        if (optionalBusinessRule.isEmpty())
            return null;

        BusinessRule businessRule = optionalBusinessRule.get();

        businessRule.setVehicleType(businessRule.getVehicleType());
        businessRule.setMinValueOfRange(businessRuleDTOWithoutId.minValueOfRange());
        businessRule.setMaxValueOfRange(businessRuleDTOWithoutId.maxValueOfRange());
        businessRule.setExtraFeeValue(businessRuleDTOWithoutId.extraFeeValue());
        businessRule.setWeatherConditionType(businessRule.getWeatherConditionType());
        businessRule.setPhenomenonType(businessRule.getPhenomenonType());

        return businessRuleDTOMapper.BusinessRuleToDTO(businessRuleRepository.save(businessRule));
    }

    public String removeBusinessRule(long id) {
        Optional<BusinessRule> optionalBusinessRule = businessRuleRepository.findById(id);
        if (optionalBusinessRule.isEmpty())
            return "Business rule with id = " + id + " doesn't exist";
        businessRuleRepository.delete(optionalBusinessRule.get());
        return "Business rule deleted successfully";
    }

    private boolean businessRuleRangesAreValid(List<BusinessRule> businessRules, double rangeMin, double rangeMax) {
        if (rangeMin > rangeMax)
            return false;

        for (BusinessRule businessRule : businessRules) {
            if ((businessRule.getMinValueOfRange() >= rangeMin && businessRule.getMinValueOfRange() <= rangeMax)||
                    (businessRule.getMaxValueOfRange() >= rangeMin && businessRule.getMaxValueOfRange() <= rangeMax))
                return false;
        }
        return true;
    }

}
