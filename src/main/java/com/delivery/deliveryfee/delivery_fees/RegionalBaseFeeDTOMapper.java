package com.delivery.deliveryfee.delivery_fees;

public class RegionalBaseFeeDTOMapper {
    public RegionalBaseFeeDTO regionalBaseFeeToDTO(RegionalBaseFee regionalBaseFee) {
        return new RegionalBaseFeeDTO(
                regionalBaseFee.getId(),
                regionalBaseFee.getCityName(),
                regionalBaseFee.getVehicleType(),
                regionalBaseFee.getBaseFeeValue()
        );
    }

    public RegionalBaseFee DTOWithoutIdToRegionalBaseFee(
            RegionalBaseFeeDTOWithoutId regionalBaseFeeDTOWithoutId) {
        return new RegionalBaseFee(
                regionalBaseFeeDTOWithoutId.cityName(),
                regionalBaseFeeDTOWithoutId.vehicleType(),
                regionalBaseFeeDTOWithoutId.baseFeeValue()
        );
    }
}
