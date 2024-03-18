package com.delivery.deliveryfee.business_rules;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
