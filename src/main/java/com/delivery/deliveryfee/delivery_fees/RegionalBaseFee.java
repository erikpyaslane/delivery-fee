package com.delivery.deliveryfee.delivery_fees;


import com.delivery.deliveryfee.enums.VehicleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "regional_base_fees")
public class RegionalBaseFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "city_name")
    @NotEmpty
    @NotNull
    private String cityName;
    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type")
    @NotNull
    private VehicleType vehicleType;
    @Column(name = "base_fee_value")
    @NotNull
    @DecimalMin(value = "0.0", message = "Base fee value cannot be negative")
    private double baseFeeValue;


    public RegionalBaseFee() {
    }

    public RegionalBaseFee(String cityName, VehicleType vehicleType, double baseFeeValue) {
        this.cityName = cityName;
        this.vehicleType = vehicleType;
        this.baseFeeValue = baseFeeValue;
    }

    public long getId() {
        return id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public double getBaseFeeValue() {
        return baseFeeValue;
    }

    public void setBaseFeeValue(double baseFeeValue) {
        this.baseFeeValue = baseFeeValue;
    }
}
