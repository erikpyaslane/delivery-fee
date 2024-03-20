package com.delivery.deliveryfee.business_rules;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/business_rules")
public class BusinessRuleController {

    private final BusinessRuleService businessRuleService;

    @Autowired
    public BusinessRuleController(BusinessRuleService businessRuleService) {
        this.businessRuleService = businessRuleService;
    }

    /**
     * Retrieves all business rules
     *
     * @return A list of all business rules
     * @example /api/business_rules/all
     */
    @GetMapping("/all")
    public List<BusinessRuleDTO> getAllBusinessRules() {
        return businessRuleService.getAllBusinessRules();
    }

    /**
     * Creates new business rule
     *
     * @param businessRuleDTOWithoutId DTO of BusinessRule class
     * @return ResponseEntity with message and HTTP status.
     * 400 Bad Request -> if incorrect request
     * 201 Created -> if business rule was created
     *
     * @example POST /api/business_rules
     * {
     *     "vehicleType": "SCOOTER",
     *     "minValueOfRange": -10.0,
     *     "maxValueOfRange": 0.0,
     *     "extraFeeValue": 1.0,
     *     "weatherConditionType": "ATEF",
     *     "phenomenonType": null
     * }
     */
    @PostMapping
    public ResponseEntity<BusinessRuleDTO> createBusinessRule(
            @RequestBody BusinessRuleDTOWithoutId businessRuleDTOWithoutId) {
        BusinessRuleDTO createdBusinessRuleDTO = businessRuleService
                .saveBusinessRule(businessRuleDTOWithoutId);
        if (createdBusinessRuleDTO == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(createdBusinessRuleDTO, HttpStatus.CREATED);
    }

    /**
     * Updates an existing business rule
     *
     * @param id The ID of business rule to update
     * @param businessRuleDTOWithoutId The updated business rule DTO
     * @return Updated business rule with HTTP status
     * @example PUT /api/business_rules/1
     * {
     *     "cityName": "Tartu",
     *     "vehicleType": "SCOOTER",
     *     "baseFeeValue": 3.0
     * }
     */
    @PutMapping("/{id}")
    public ResponseEntity<BusinessRuleDTO> updateBusinessRule(
            @PathVariable long id,
            @RequestBody BusinessRuleDTOWithoutId businessRuleDTOWithoutId) {
        BusinessRuleDTO updatedBusinessRule = businessRuleService.updateBusinessRule(id, businessRuleDTOWithoutId);
        return new ResponseEntity<>(updatedBusinessRule, HttpStatus.OK);
    }

    /**
     * Deletes a business rule by ID
     *
     * @param id The ID of the business rule to delete
     * @return A response indicating success or failure of the operation.
     * 204 No Content -> if deleted successfully
     * 400 Bad Request -> if failed
     * @example DELETE /api/business_rules/1
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBusinessRule(@PathVariable long id){
        return businessRuleService.removeBusinessRule(id);
    }
}
