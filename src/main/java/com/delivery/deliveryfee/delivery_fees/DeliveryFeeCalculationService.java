package com.delivery.deliveryfee.delivery_fees;


import com.delivery.deliveryfee.business_rules.BusinessRule;
import com.delivery.deliveryfee.business_rules.BusinessRuleDTO;
import com.delivery.deliveryfee.business_rules.BusinessRuleService;
import com.delivery.deliveryfee.enums.PhenomenonType;
import com.delivery.deliveryfee.enums.VehicleType;
import com.delivery.deliveryfee.enums.WeatherConditionType;
import com.delivery.deliveryfee.exceptions.NoSuchVehicleTypeException;
import com.delivery.deliveryfee.station_city_mapping.StationCityMappingService;
import com.delivery.deliveryfee.weather_observations.WeatherObservationDTO;
import com.delivery.deliveryfee.weather_observations.WeatherObservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveryFeeCalculationService {

    private final WeatherObservationService weatherObservationService;
    private final BusinessRuleService businessRuleService;
    private final RegionalBaseFeeRepository regionalBaseFeeRepository;

    private final StationCityMappingService stationCityMappingService;


    @Autowired
    public DeliveryFeeCalculationService(WeatherObservationService weatherObservationService,
                                         BusinessRuleService businessRuleService,
                                         RegionalBaseFeeRepository regionalBaseFeeRepository,
                                         StationCityMappingService stationCityMappingService) {
        this.weatherObservationService = weatherObservationService;
        this.businessRuleService = businessRuleService;
        this.regionalBaseFeeRepository = regionalBaseFeeRepository;
        this.stationCityMappingService = stationCityMappingService;
    }

    public void saveRGB(String cityName, String scooterRGB, String bikeRGB, String carRGB) {
        System.out.println("Hi!");
    }

    public List<RegionalBaseFee> getRegionalBaseFees() {
        return regionalBaseFeeRepository.findAll();
    }

    public double getCalculationFee(String cityName, String vehicleType) {

        VehicleType convertedVehicleType = null;
        double totalFee = 0.0;

        try {
            convertedVehicleType = VehicleType.valueOf(vehicleType);
            totalFee = calculateTotalFee(cityName, convertedVehicleType);
        } catch (IllegalArgumentException e) {
            throw new NoSuchVehicleTypeException("No such vehicle type");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("Vehicle type: " + convertedVehicleType);
        return totalFee;
    }

    public double calculateTotalFee(String cityName, VehicleType vehicleType) throws Exception {

        WeatherObservationDTO weatherObservation = null;
        weatherObservation = weatherObservationService
                .getLatestObservationByCityName(cityName);
        double totalFee = 0.0;
        totalFee += calculateRBF(cityName, vehicleType);
        System.out.println("RBF: " + totalFee);
        totalFee += calculateExtraFee(weatherObservation, vehicleType);
        return totalFee;
    }


    private double calculateRBF(String cityName, VehicleType vehicleType) {
        RegionalBaseFee regionalBaseFee = getRegionalBaseFeeByCityNameAndVehicleType(cityName, vehicleType);
        System.out.println(regionalBaseFee);
        return regionalBaseFee.getBaseFeeValue();

    }

    private RegionalBaseFee getRegionalBaseFeeByCityNameAndVehicleType(String cityName, VehicleType vehicleType) {
        return regionalBaseFeeRepository.findTopByCityNameAndVehicleType(cityName, vehicleType);
    }


    private double calculateExtraFee (WeatherObservationDTO weatherObservation, VehicleType vehicleType) throws Exception {
        double totalExtraFee = 0;
        System.out.println("Phenomenon type: " + weatherObservation.weatherPhenomenon());
        totalExtraFee += calculateATEF(weatherObservation.airTemperature(), vehicleType);
        totalExtraFee += calculateWSEF(weatherObservation.windSpeed(), vehicleType);
        totalExtraFee += calculateWPEF(
                vehicleType, PhenomenonType.getPhenomenonType(weatherObservation.weatherPhenomenon())
        );

        return totalExtraFee;
    }

    private double calculateATEF(double air, VehicleType vehicleType) {
        BusinessRuleDTO businessRuleDTO;
        businessRuleDTO = businessRuleService
                .getBusinessRuleByVehicleTypeAndWeatherConditionTypeAndRangeValue(
                        vehicleType, WeatherConditionType.ATEF, air);
        System.out.println("Air temperature: " + air);
        if (businessRuleDTO == null){
            System.out.println("ATEF: " + 0.0);
            return 0.0;
        }
        System.out.println("ATEF: " + businessRuleDTO.extraFeeValue());
        return businessRuleDTO.extraFeeValue();
    }

    private double calculateWSEF(double wind, VehicleType vehicleType) {
        BusinessRuleDTO businessRuleDTO;
        businessRuleDTO = businessRuleService
                .getBusinessRuleByVehicleTypeAndWeatherConditionTypeAndRangeValue(
                        vehicleType, WeatherConditionType.WSEF, wind);
        System.out.println("Wind speed: " + wind);
        if (businessRuleDTO == null){
            System.out.println("WSEF: " + 0.0);
            return 0.0;
        }
        System.out.println("WSEF: " + businessRuleDTO.extraFeeValue());
        return businessRuleDTO.extraFeeValue();
    }


    private double calculateWPEF(VehicleType vehicleType, PhenomenonType phenomenonType) {
        System.out.println("Phenomenon type: " + phenomenonType);
        if (phenomenonType == PhenomenonType.NOPE){
            return 0.0;
        }
        BusinessRuleDTO businessRuleDTO;
        businessRuleDTO = businessRuleService
                .getBusinessRuleByVehicleTypeAndPhenomenonType(vehicleType, phenomenonType);
        if (businessRuleDTO == null){
            System.out.println("WPEF: " + 0.0);
            return 0.0;
        }
        System.out.println("WPEF: " + businessRuleDTO.extraFeeValue());
        return businessRuleDTO.extraFeeValue();
    }


    public void updateRBF(RegionalBaseFee updatedDetails) {
        System.out.println("In progress " + updatedDetails);

    }
}