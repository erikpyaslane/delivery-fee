package com.delivery.deliveryfee.delivery_fees;


import com.delivery.deliveryfee.business_rules.BusinessRuleDTO;
import com.delivery.deliveryfee.business_rules.BusinessRuleService;
import com.delivery.deliveryfee.enums.PhenomenonType;
import com.delivery.deliveryfee.enums.VehicleType;
import com.delivery.deliveryfee.enums.WeatherConditionType;
import com.delivery.deliveryfee.exceptions.NoUsageAllowedException;
import com.delivery.deliveryfee.weather_observations.WeatherObservationDTO;
import com.delivery.deliveryfee.weather_observations.WeatherObservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Service
public class DeliveryFeeCalculationService {

    private final WeatherObservationService weatherObservationService;
    private final BusinessRuleService businessRuleService;
    private final RegionalBaseFeeRepository regionalBaseFeeRepository;

    @Autowired
    public DeliveryFeeCalculationService(WeatherObservationService weatherObservationService,
                                         BusinessRuleService businessRuleService,
                                         RegionalBaseFeeRepository regionalBaseFeeRepository
    ) {
        this.weatherObservationService = weatherObservationService;
        this.businessRuleService = businessRuleService;
        this.regionalBaseFeeRepository = regionalBaseFeeRepository;
    }

    /**
     * Retrieves all regional base fees
     *
     * @return list of all regional base fees
     */
    public List<RegionalBaseFee> getRegionalBaseFees() {
        return regionalBaseFeeRepository.findAll();
    }

    /**
     * Calculates delivery fee relating to city name, vehicle type and the latest weather data
     *
     * @param cityName name of city
     * @param vehicleType name of vehicle
     * @return response with calculated delivery fee
     */
    public ResponseEntity<String> getCalculationFee(String cityName, String vehicleType, String localDateTime) {

        VehicleType convertedVehicleType;
        double totalFee;
        LocalDateTime convertedDateTime;

        try {
            convertedDateTime = converLocalDateTime(localDateTime);
            convertedVehicleType = VehicleType.valueOf(vehicleType.toUpperCase());
            totalFee = calculateTotalFee(cityName, convertedVehicleType, convertedDateTime);
        } catch (NoUsageAllowedException e) {
            throw new NoUsageAllowedException("Usage of selected vehicle type is forbidden");
        } catch (DateTimeParseException e) {
            throw new DateTimeParseException("Invalid datetime format", localDateTime, 0);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("No such vehicle type exist");
        }

        System.out.println("Vehicle type: " + convertedVehicleType);
        return new ResponseEntity<>("Delivery fee : " + totalFee, HttpStatus.OK);
    }

    private LocalDateTime converLocalDateTime(String stringDateTime) throws DateTimeParseException {
        if (stringDateTime.isEmpty())
            return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return LocalDateTime.parse(stringDateTime, formatter);
    }

    /**
     * Calculates total delivery fee
     *
     * @param cityName target city
     * @param vehicleType target vehicle
     * @param localDateTime time of weather observation
     * @return calculated total delivery fee
     * @throws NoUsageAllowedException if phenomenon type  forbids delivery on target vehicle
     */
    public double calculateTotalFee(String cityName, VehicleType vehicleType, LocalDateTime localDateTime)
            throws NoUsageAllowedException {

        WeatherObservationDTO weatherObservation;
        if (localDateTime == null)
            weatherObservation = weatherObservationService
                    .getLatestObservationByCityName(cityName);
        else
            weatherObservation = weatherObservationService
                    .getWeatherObservationByCityNameAndTimeOfObservation(cityName, localDateTime);
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

    private double calculateExtraFee (WeatherObservationDTO weatherObservation, VehicleType vehicleType)
            throws NoUsageAllowedException {

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

    private double calculateWPEF(VehicleType vehicleType, PhenomenonType phenomenonType) throws NoUsageAllowedException {
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
        if (businessRuleDTO.extraFeeValue() == -1.0)
            throw new NoUsageAllowedException("Usage of selected vehicle type is forbidden");
        return businessRuleDTO.extraFeeValue();
    }

    /**
     * Updates regional base fee entity
     *
     * @param id The ID of regional base fee to update
     * @param updatedDetails updated regional base fee
     * @return response with message of updating or failing
     */
    public ResponseEntity<String> updateRegionalBaseFee(long id, RegionalBaseFeeDTOWithoutId updatedDetails) {
        Optional<RegionalBaseFee> optionalRegionalBaseFee = regionalBaseFeeRepository.findById(id);

        if (optionalRegionalBaseFee.isEmpty())
            return new ResponseEntity<>("No object with id = " + id, HttpStatus.BAD_REQUEST);

        RegionalBaseFee regionalBaseFee = optionalRegionalBaseFee.get();

        if (!regionalBaseFee.getCityName().equals(updatedDetails.cityName()) ||
        !regionalBaseFee.getVehicleType().equals(updatedDetails.vehicleType()))
            return new ResponseEntity<>("Can't overwrite city name", HttpStatus.BAD_REQUEST);

        if (updatedDetails.baseFeeValue() < 0)
            return new ResponseEntity<>("Base fee can't be negative", HttpStatus.BAD_REQUEST);

        regionalBaseFee.setBaseFeeValue(updatedDetails.baseFeeValue());
        regionalBaseFeeRepository.save(regionalBaseFee);
        return new ResponseEntity<>("Regional Base Fee was successfully updated!", HttpStatus.OK);
    }
}