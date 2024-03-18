package com.delivery.deliveryfee.business_rules;

import com.delivery.deliveryfee.enums.VehicleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
        List<BusinessRule> businessRules = getBusinessRulesByVehicleType(convertedVehicleType);
        return businessRules.stream()
                .map(businessRuleDTOMapper::BusinessRuleToDTO)
                .collect(Collectors.toList());
    }

    private List<BusinessRule> getBusinessRulesByVehicleType(VehicleType vehicleType) {
        return businessRuleRepository.findBusinessRulesByVehicleType(vehicleType);
    }

}
