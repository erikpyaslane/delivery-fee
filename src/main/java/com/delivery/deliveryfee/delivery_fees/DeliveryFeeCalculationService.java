package com.delivery.deliveryfee.delivery_fees;


import com.delivery.deliveryfee.enums.VehicleType;
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
    //private final BusinessRuleService businessRuleService;
    private final RegionalBaseFeeRepository regionalBaseFeeRepository;

    private final StationCityMappingService stationCityMappingService;


    @Autowired
    public DeliveryFeeCalculationService(WeatherObservationService weatherObservationService,
                                         //BusinessRuleService businessRuleService,
                                         RegionalBaseFeeRepository regionalBaseFeeRepository,
                                         StationCityMappingService stationCityMappingService) {
        this.weatherObservationService = weatherObservationService;
        //this.businessRuleService = businessRuleService;
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
            System.out.println(convertedVehicleType);
            totalFee = calculateTotalFee(cityName, convertedVehicleType);
        } catch (IllegalArgumentException e) {
            throw new NoSuchVehicleTypeException("No such vehicle type");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println(convertedVehicleType);
        return totalFee;
    }

    public double calculateTotalFee(String cityName, VehicleType vehicleType) throws Exception {

        WeatherObservationDTO weatherObservation = null;
        weatherObservation = weatherObservationService
                .getLatestObservationByCityName(cityName);
        double totalFee = 0.0;
        totalFee += calculateRBF(cityName, vehicleType);
        System.out.println(totalFee);
        //totalFee += calculateExtraFee(weatherObservation, vehicleType);

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

    /*
    private double calculateExtraFee (WeatherObservationDTO weatherObservation, VehicleType vehicleType) throws Exception {
        double totalExtraFee = 0;
        System.out.println(weatherObservation.airTemperature());
        //totalExtraFee += calculateATEF(weatherObservation.airTemperature(), vehicleType);
        //totalExtraFee += calculateWSEF(weatherObservation.windSpeed(), vehicleType);
        //totalExtraFee += calculateWPEF(weatherObservation.getWeatherPhenomenon());

        return totalExtraFee;
    }

    private double calculateATEF(double air, VehicleType vehicleType) {
        BusinessRuleDTO businessRuleDTO;
        businessRuleDTO = businessRuleService
                .getBusinessRuleByVehicleTypeAndWeatherConditionTypeAndRangeValue(
                        vehicleType, WeatherConditionType.ATEF, air);
        if (businessRuleDTO == null)
            return 0.0;
        System.out.println("ATEF: " + businessRuleDTO.extraFeeValue());
        return businessRuleDTO.extraFeeValue();
    }
    private double calculateWSEF(double wind, VehicleType vehicleType) {
        BusinessRuleDTO businessRuleDTO;
        businessRuleDTO = businessRuleService
                .getBusinessRuleByVehicleTypeAndWeatherConditionTypeAndRangeValue(
                        vehicleType, WeatherConditionType.WSEF, wind);
        if (businessRuleDTO == null)
            return 0.0;
        return businessRuleDTO.extraFeeValue();
    }
    private double calculateWPEF(String phenomenon) {
        if (phenomenon.equals("Car"))
            return 0.0;

        return 0;
    }


    public void updateRBF(RegionalBaseFee updatedDetails) {
        System.out.println("In progress " + updatedDetails);

    }
     */
    /*
    public List<RegionalBaseFeeDTO> getRGBsByCityName(String cityName) {
        Optional<List<RegionalBaseFee>> optionalRegionalBaseFees = regionalBaseFeeRepository
                .findAllByCityName(cityName);
        if (optionalRegionalBaseFees.isEmpty())
            return null;
        return optionalRegionalBaseFees.stream()
                .map()
    }
     */
}
