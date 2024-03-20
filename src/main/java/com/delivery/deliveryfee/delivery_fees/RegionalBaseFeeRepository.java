package com.delivery.deliveryfee.delivery_fees;

import com.delivery.deliveryfee.enums.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionalBaseFeeRepository extends JpaRepository<RegionalBaseFee, Long> {

    RegionalBaseFee findTopByCityNameAndVehicleType(String cityName, VehicleType vehicleType);

}
