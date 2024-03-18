package com.delivery.deliveryfee.business_rules;

import com.delivery.deliveryfee.enums.VehicleType;
import com.delivery.deliveryfee.enums.WeatherConditionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BusinessRuleRepository extends JpaRepository<BusinessRule, Long> {

    List<BusinessRule> findBusinessRulesByVehicleType(VehicleType vehicleType);

    List<BusinessRule> findBusinessRulesByWeatherConditionType(WeatherConditionType weatherConditionType);

    List<BusinessRule> findBusinessRulesByVehicleTypeAndWeatherConditionType(
            VehicleType vehicleType, WeatherConditionType weatherConditionType);

    @Query("SELECT br FROM BusinessRule br WHERE " +
            "br.vehicleType = :vehicleType " +
            "AND br.weatherConditionType = :weatherConditionType " +
            "AND br.minValueOfRange <= :rangeValue " +
            "AND br.maxValueOfRange >= :rangeValue")
    BusinessRule findBusinessRuleByVehicleTypeAndWeatherConditionTypeAndRangeValue(
            VehicleType vehicleType, WeatherConditionType weatherConditionType, double rangeValue
    );

    Optional<BusinessRule> findById(long id);

}
