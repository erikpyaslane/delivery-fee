package com.delivery.deliveryfee.business_rules;

import com.delivery.deliveryfee.enums.VehicleType;
import com.delivery.deliveryfee.enums.WeatherConditionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BusinessRuleRepository extends JpaRepository<BusinessRule, Long> {

    List<BusinessRule> findBusinessRulesByVehicleType(VehicleType vehicleType);

}
