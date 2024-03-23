package com.delivery.deliveryfee.delivery_fees;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/delivery_fees")
public class DeliveryFeeCalculationController {
    private final DeliveryFeeCalculationService deliveryFeeCalculationService;

    @Autowired
    public DeliveryFeeCalculationController(DeliveryFeeCalculationService deliveryFeeCalculationService) {
        this.deliveryFeeCalculationService = deliveryFeeCalculationService;
    }

    /**
     * Calculates delivery fee by request parameters
     *
     * @param cityName name of city
     * @param vehicleType type of vehicle
     *
     * @return response message with calculated delivery fee
     * @example GET /api/delivery_fees?cityName=Tartu&vehicleType=SCOOTER
     */
    @GetMapping("")
    public ResponseEntity<String> getDeliveryFee(@RequestParam("cityName") String cityName,
                                                 @RequestParam("vehicleType") String vehicleType,
                                                 @RequestParam(value="timestamp", required = false) String localDateTime) {
        return deliveryFeeCalculationService.getCalculationFee(
                cityName, vehicleType, Objects.requireNonNullElse(localDateTime, "")
        );
    }

    /**
     * Retrieves all RBFs
     *
     * @return list of regional base fees
     * @example /delivery_fees/all
     */
    @GetMapping("/all")
    public List<RegionalBaseFee> getRegionalBaseFees() {
        return deliveryFeeCalculationService.getRegionalBaseFees();
    }

    /**
     * Updates regional base fee
     *
     * @param id The ID of regional base fee object
     * @param regionalBaseFee the updated regional base fee DTO
     * @return updated regional base fee with HTTP status
     */
    @PutMapping("/{id}")
    ResponseEntity<String> updateRegionalBaseFeeOfCity(
            @PathVariable long id, @RequestBody RegionalBaseFeeDTOWithoutId regionalBaseFee) {
        return deliveryFeeCalculationService.updateRegionalBaseFee(id, regionalBaseFee);
    }
}
