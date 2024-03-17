package com.delivery.deliveryfee.delivery_fees;

import com.delivery.deliveryfee.enums.VehicleType;

public record RegionalBaseFeeDTO(
        Long id,
        String cityName,
        VehicleType vehicleType,
        Double baseFeeValue
) {
}
