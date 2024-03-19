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

    @GetMapping("/all")
    public List<BusinessRuleDTO> getAllBusinessRules() {
        return businessRuleService.getAllBusinessRules();
    }

    @GetMapping("/{vehicleType}")
    public ResponseEntity<List<BusinessRuleDTO>> getBusinessRulesByVehicleType(@PathVariable String vehicleType) {
        return new ResponseEntity<>(
                businessRuleService.getBusinessRulesByVehicleType(vehicleType), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<BusinessRuleDTO> createBusinessRule(
            @RequestBody BusinessRuleDTOWithoutId businessRuleDTOWithoutId) {
        BusinessRuleDTO createdBusinessRuleDTO = businessRuleService
                .saveBusinessRule(businessRuleDTOWithoutId);
        if (createdBusinessRuleDTO == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(createdBusinessRuleDTO, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<BusinessRuleDTO> updateBusinessRule(
            @PathVariable long id,
            @RequestBody BusinessRuleDTOWithoutId businessRuleDTOWithoutId) {
        BusinessRuleDTO updatedBusinessRule = businessRuleService.updateBusinessRule(id, businessRuleDTOWithoutId);
        return new ResponseEntity<>(updatedBusinessRule, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBusinessRule(@PathVariable long id){
        return new ResponseEntity<>(businessRuleService.removeBusinessRule(id), HttpStatus.NO_CONTENT);
    }
}
