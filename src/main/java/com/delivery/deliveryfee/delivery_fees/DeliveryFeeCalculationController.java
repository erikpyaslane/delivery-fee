package com.delivery.deliveryfee.delivery_fees;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/delivery_fees")
public class DeliveryFeeCalculationController {
    private final DeliveryFeeCalculationService deliveryFeeCalculationService;

    @Autowired
    public DeliveryFeeCalculationController(DeliveryFeeCalculationService deliveryFeeCalculationService) {
        this.deliveryFeeCalculationService = deliveryFeeCalculationService;
    }

    @GetMapping("")
    public String getDeliveryFee(@RequestParam("cityName") String cityName,
                                 @RequestParam("vehicleType") String vehicleType) {
        System.out.println(cityName);
        System.out.println(vehicleType);
        return "Delivery_fee: " + deliveryFeeCalculationService.getCalculationFee(cityName, vehicleType);
    }

    @GetMapping("/all")
    public List<RegionalBaseFee> getDeliveryFee() {
        return deliveryFeeCalculationService.getRegionalBaseFees();
    }
    /*
    @GetMapping("/base_fee/{cityName}")
    public List<RegionalBaseFeeDTO> getRegionalBaseFee(@PathVariable String cityName) {
        return deliveryFeeCalculationService.getRGBsByCityName(cityName);
    }



    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    void updateRBFofCity(@Valid @RequestBody RegionalBaseFee regionalBaseFee) {
        deliveryFeeCalculationService.updateRBF(regionalBaseFee);
    }
 */
}
