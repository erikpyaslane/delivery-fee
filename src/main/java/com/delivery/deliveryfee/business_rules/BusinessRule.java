package com.delivery.deliveryfee.business_rules;

import com.delivery.deliveryfee.enums.PhenomenonType;
import com.delivery.deliveryfee.enums.VehicleType;
import com.delivery.deliveryfee.enums.WeatherConditionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "business_rules")
public class BusinessRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Enumerated(EnumType.STRING)
    @Column(name="vehicle_type")
    private VehicleType vehicleType;

    @Column(name="min_value")
    private Double minValueOfRange;
    @Column(name="max_value")
    private Double maxValueOfRange;
    @Column(name="extra_fee_value")
    @NotNull
    private double extraFeeValue;
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "weather_condition_type")
    private WeatherConditionType weatherConditionType;
    @Enumerated(EnumType.STRING)
    @Column(name = "phenomenon_type")
    private PhenomenonType phenomenonType;

    public BusinessRule() {
    }

    public BusinessRule(VehicleType vehicleType, Double minValueOfRange, Double maxValueOfRange, double extraFeeValue, WeatherConditionType weatherConditionType, PhenomenonType phenomenonType) {
        this.vehicleType = vehicleType;
        this.minValueOfRange = minValueOfRange;
        this.maxValueOfRange = maxValueOfRange;
        this.extraFeeValue = extraFeeValue;
        this.weatherConditionType = weatherConditionType;
        this.phenomenonType = phenomenonType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Double getMinValueOfRange() {
        return minValueOfRange;
    }

    public void setMinValueOfRange(Double minValueOfRange) {
        this.minValueOfRange = minValueOfRange;
    }

    public Double getMaxValueOfRange() {
        return maxValueOfRange;
    }

    public void setMaxValueOfRange(Double maxValueOfRange) {
        this.maxValueOfRange = maxValueOfRange;
    }

    public double getExtraFeeValue() {
        return extraFeeValue;
    }

    public void setExtraFeeValue(double extraFeeValue) {
        this.extraFeeValue = extraFeeValue;
    }

    public WeatherConditionType getWeatherConditionType() {
        return weatherConditionType;
    }

    public void setWeatherConditionType(WeatherConditionType weatherConditionType) {
        this.weatherConditionType = weatherConditionType;
    }

    public PhenomenonType getPhenomenonType() {
        return phenomenonType;
    }

    public void setPhenomenonType(PhenomenonType phenomenonType) {
        this.phenomenonType = phenomenonType;
    }
}
