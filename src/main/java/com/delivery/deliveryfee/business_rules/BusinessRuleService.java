package com.delivery.deliveryfee.business_rules;

import com.delivery.deliveryfee.delivery_fees.DeliveryFeeCalculationService;
import com.delivery.deliveryfee.enums.PhenomenonType;
import com.delivery.deliveryfee.enums.VehicleType;
import com.delivery.deliveryfee.enums.WeatherConditionType;
import com.delivery.deliveryfee.exceptions.WrongWeatherConditionRangeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BusinessRuleService {

    private static final Logger logger = LogManager.getLogger(BusinessRuleService.class);

    private final BusinessRuleRepository businessRuleRepository;
    private final BusinessRuleDTOMapper businessRuleDTOMapper;

    @Autowired
    public BusinessRuleService(BusinessRuleRepository businessRuleRepository, BusinessRuleDTOMapper businessRuleDTOMapper) {
        this.businessRuleRepository = businessRuleRepository;
        this.businessRuleDTOMapper = businessRuleDTOMapper;
    }


    /**
     * Retrieves all business rules
     *
     * @return list of business rules
     */
    public List<BusinessRuleDTO> getAllBusinessRules() {
        List<BusinessRule> businessRules = businessRuleRepository.findAll();
        return businessRules.stream()
                .map(businessRuleDTOMapper::BusinessRuleToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves business rule with target vehicle type and phenomenon type
     *
     * @param vehicleType target vehicle
     * @param phenomenonType target phenomenon
     * @return business rule with given parameters or null if not exist
     */
    public BusinessRuleDTO getBusinessRuleByVehicleTypeAndPhenomenonType(
            VehicleType vehicleType, PhenomenonType phenomenonType) {

        Optional<BusinessRule> optionalBusinessRule = businessRuleRepository
                .findBusinessRuleByVehicleTypeAndPhenomenonType(vehicleType, phenomenonType);

        return optionalBusinessRule.map(businessRuleDTOMapper::BusinessRuleToDTO).orElse(null);
    }


    /**
     * Retireves business rule by vehicle type weather condition(ATEF/WSEF) and its value(air/wind)
     *
     * @param vehicleType target vehicle type
     * @param weatherConditionType target weather condition ATEF or WSEF
     * @param rangeValue Air temperature or wind speed value
     * @return business rule that covers all these conditions
     */
    public BusinessRuleDTO getBusinessRuleByVehicleTypeAndWeatherConditionTypeAndRangeValue(
            VehicleType vehicleType, WeatherConditionType weatherConditionType, double rangeValue) {
        Optional<BusinessRule> optionalBusinessRule = businessRuleRepository
                .findBusinessRuleByVehicleTypeAndWeatherConditionTypeAndRangeValue(
                        vehicleType, weatherConditionType, rangeValue
                );

        return optionalBusinessRule.map(businessRuleDTOMapper::BusinessRuleToDTO).orElse(null);
    }

    /**
     * Saves created business rule
     *
     * @param businessRuleDTOWithoutId DTO of business rule
     * @return business rule if it was successfully created or null if creation was failed
     */
    public BusinessRuleDTO saveBusinessRule(BusinessRuleDTOWithoutId businessRuleDTOWithoutId) {
        BusinessRule businessRule = businessRuleDTOMapper.DTOToBusinessRule(businessRuleDTOWithoutId);
        List<BusinessRule> businessRules = businessRuleRepository
                .findBusinessRulesByVehicleTypeAndWeatherConditionType(
                        businessRule.getVehicleType(), businessRule.getWeatherConditionType());

        try {
            if (businessRules.isEmpty())
                return businessRuleDTOMapper.BusinessRuleToDTO(
                        businessRuleRepository.save(businessRule));
            else if (businessRuleRangesAreValid(businessRules, businessRuleDTOWithoutId.weatherConditionType(),
                    businessRule.getMinValueOfRange(), businessRule.getMaxValueOfRange())){
                if ((businessRule.getMaxValueOfRange() == null || businessRule.getMinValueOfRange() == null)
                        && businessRule.getWeatherConditionType() != WeatherConditionType.WPEF)
                    businessRule = overlapBusinessRules(businessRules, businessRule);
                if (businessRule == null)
                    throw new WrongWeatherConditionRangeException(
                            "There is already exist a business rule that crosses current range!");
                return businessRuleDTOMapper.BusinessRuleToDTO(
                        businessRuleRepository.save(businessRule));
            }
        } catch (WrongWeatherConditionRangeException e) {
            logger.info(e.getMessage());
        }
        return null;
    }

    /**
     * Updates chosen business rule
     *
     * @param id The ID of target business rule
     * @param businessRuleDTOWithoutId DTO of business rule with updated data
     * @return DTO of updated business rule or null if updating failed
     */
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

        logger.info("Business rule updated!");
        return businessRuleDTOMapper.BusinessRuleToDTO(businessRuleRepository.save(businessRule));
    }

    /**
     * Removes business rule
     *
     * @param id The ID of business rule to delete
     * @return response message with code 204 No Content if deletion completed, 401 Bad Request if deletion failed
     */
    public ResponseEntity<String> removeBusinessRule(long id) {
        Optional<BusinessRule> optionalBusinessRule = businessRuleRepository.findById(id);
        if (optionalBusinessRule.isEmpty())
            return new ResponseEntity<>(
                    "Business rule with id = " + id + " doesn't exist", HttpStatus.BAD_REQUEST);

        businessRuleRepository.delete(optionalBusinessRule.get());
        logger.info("Business rule was removed!");
        return new ResponseEntity<>("Business rule deleted successfully", HttpStatus.NO_CONTENT);
    }

    private boolean businessRuleRangesAreValid(List<BusinessRule> businessRules,
                                               WeatherConditionType weatherConditionType,
                                               Double rangeMin, Double rangeMax)
            throws WrongWeatherConditionRangeException {

        if (weatherConditionType == WeatherConditionType.WPEF) {
            return rangeMin == null && rangeMax == null;
        }
        if (rangeMin == null)
            rangeMin = -Double.MAX_VALUE;
        if (rangeMax == null)
            rangeMax = Double.MAX_VALUE;
        logger.info("Max: " + rangeMax + " min: " + rangeMin + (rangeMax > rangeMin));
        if (rangeMin > rangeMax) {
            logger.error("Wrong range values");
            throw new WrongWeatherConditionRangeException(
                    "Range minimum value can't be greater than range maximum value");
        }

        for (BusinessRule businessRule : businessRules) {
            if (businessRule.getMinValueOfRange() == null){
                if (businessRule.getMaxValueOfRange() > rangeMin &&
                    businessRule.getMaxValueOfRange() < rangeMax)
                    throw new WrongWeatherConditionRangeException("Wrong range values");
            } else if (businessRule.getMaxValueOfRange() == null) {
                if (businessRule.getMinValueOfRange() > rangeMin &&
                businessRule.getMinValueOfRange() < rangeMax)
                    throw new WrongWeatherConditionRangeException("Wrong range values");
            } else if ((businessRule.getMinValueOfRange() >= rangeMin && businessRule.getMinValueOfRange() <= rangeMax) ||
                    (businessRule.getMaxValueOfRange() >= rangeMin && businessRule.getMaxValueOfRange() <= rangeMax))
                throw new WrongWeatherConditionRangeException(
                        "There is already exist a business rule with the same weather condition range!"
                );
        }
        System.out.println(true);
        return true;
    }

    private BusinessRule overlapBusinessRules (List<BusinessRule> businessRules, BusinessRule businessRule) {

        for (BusinessRule bRule : businessRules) {
            if (businessRule.getMinValueOfRange() == null) {
                if (bRule.getMinValueOfRange() == null &&
                        bRule.getMaxValueOfRange() > businessRule.getMaxValueOfRange()) {
                    bRule.setMinValueOfRange(businessRule.getMaxValueOfRange());
                    businessRuleRepository.save(bRule);
                    return businessRule;
                }
            }
            else if (businessRule.getMaxValueOfRange() == null) {
                if (bRule.getMaxValueOfRange() == null &&
                        bRule.getMinValueOfRange() < businessRule.getMinValueOfRange()) {
                    bRule.setMaxValueOfRange(businessRule.getMinValueOfRange());
                    businessRuleRepository.save(bRule);
                    return businessRule;
                }
            }
        }
        return null;
    }

}
