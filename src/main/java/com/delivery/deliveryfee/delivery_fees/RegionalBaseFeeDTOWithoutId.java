package com.delivery.deliveryfee.delivery_fees;


import com.delivery.deliveryfee.enums.VehicleType;

public record RegionalBaseFeeDTOWithoutId(

        String cityName,
        VehicleType vehicleType,
        Double baseFeeValue
) {
}
