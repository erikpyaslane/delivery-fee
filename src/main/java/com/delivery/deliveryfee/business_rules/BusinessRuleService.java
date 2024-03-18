package com.delivery.deliveryfee.business_rules;

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

    public List<BusinessRuleDTO> getBusinessRulesByWeatherConditionType(String weatherConditionType) {
        WeatherConditionType convertedWeatherConditionType = null;
        try {
            convertedWeatherConditionType = WeatherConditionType.valueOf(weatherConditionType);
        } catch (IllegalArgumentException e) {
            System.out.println("This vehicle type does not exist!");
        }
        List<BusinessRule> businessRules = businessRuleRepository
                .findBusinessRulesByWeatherConditionType(convertedWeatherConditionType);

        return businessRules.stream()
                .map(businessRuleDTOMapper::BusinessRuleToDTO)
                .collect(Collectors.toList());
    }

    public List<BusinessRuleDTO> getBusinessRulesByVehicleTypeAndWeatherConditionType(
            VehicleType vehicleType, WeatherConditionType weatherConditionType) {

        List<BusinessRule> businessRules = businessRuleRepository
                .findBusinessRulesByVehicleTypeAndWeatherConditionType(vehicleType, weatherConditionType);

        return businessRules.stream()
                .map(businessRuleDTOMapper::BusinessRuleToDTO)
                .collect(Collectors.toList());
    }


    public BusinessRuleDTO getBusinessRuleByVehicleTypeAndWeatherConditionTypeAndRangeValue(
            VehicleType vehicleType, WeatherConditionType weatherConditionType, double rangeValue) {

        return businessRuleDTOMapper.BusinessRuleToDTO(
                businessRuleRepository.findBusinessRuleByVehicleTypeAndWeatherConditionTypeAndRangeValue(
                        vehicleType, weatherConditionType, rangeValue
                ));
    }

    public BusinessRuleDTO saveBusinessRule(BusinessRuleDTOWithoutId businessRuleDTOWithoutId) {
        BusinessRule businessRule = businessRuleDTOMapper.DTOToBusinessRule(businessRuleDTOWithoutId);
        return businessRuleDTOMapper.BusinessRuleToDTO(
                businessRuleRepository.save(businessRule));
    }

    public BusinessRuleDTO updateBusinessRule(long id, BusinessRuleDTOWithoutId businessRuleDTOWithoutId) {
        Optional<BusinessRule> optionalBusinessRule = businessRuleRepository.findById(id);
        if (optionalBusinessRule.isEmpty())
            return null;

        BusinessRule businessRule = optionalBusinessRule.get();

        businessRule.setVehicleType(businessRule.getVehicleType());
        businessRule.setMinValueOFRange(businessRuleDTOWithoutId.minValueOfRange());
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

}
