package com.delivery.deliveryfee.delivery_fees;


import com.delivery.deliveryfee.business_rules.BusinessRuleDTO;
import com.delivery.deliveryfee.business_rules.BusinessRuleService;
import com.delivery.deliveryfee.enums.PhenomenonType;
import com.delivery.deliveryfee.enums.VehicleType;
import com.delivery.deliveryfee.enums.WeatherConditionType;
import com.delivery.deliveryfee.exceptions.InvalidCityNameException;
import com.delivery.deliveryfee.exceptions.NoUsageAllowedException;
import com.delivery.deliveryfee.exceptions.WeatherObservationNotFoundException;
import com.delivery.deliveryfee.station_city_mapping.StationCityMappingService;
import com.delivery.deliveryfee.weather_observations.WeatherObservationDTO;
import com.delivery.deliveryfee.weather_observations.WeatherObservationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger logger = LogManager.getLogger(DeliveryFeeCalculationService.class);

    private final WeatherObservationService weatherObservationService;
    private final BusinessRuleService businessRuleService;
    private final RegionalBaseFeeRepository regionalBaseFeeRepository;

    private final StationCityMappingService stationCityMappingService;

    @Autowired
    public DeliveryFeeCalculationService(WeatherObservationService weatherObservationService,
                                         BusinessRuleService businessRuleService,
                                         RegionalBaseFeeRepository regionalBaseFeeRepository,
                                         StationCityMappingService stationCityMappingService
    ) {
        this.weatherObservationService = weatherObservationService;
        this.businessRuleService = businessRuleService;
        this.regionalBaseFeeRepository = regionalBaseFeeRepository;
        this.stationCityMappingService = stationCityMappingService;
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

        try {
            if (cityName == null || cityName.isEmpty() ||
            !stationCityMappingService.existsCityName(cityName))
                throw new InvalidCityNameException("Invalid city input!");

            LocalDateTime convertedDateTime = converLocalDateTime(localDateTime);
            logger.info("Converted date: " + convertedDateTime);

            convertedVehicleType = VehicleType.valueOf(vehicleType.toUpperCase());
            totalFee = calculateTotalFee(cityName, convertedVehicleType, convertedDateTime);
            logger.info("Delivery fee was successfully calculated!");

        } catch (InvalidCityNameException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NullPointerException e) {
            return new ResponseEntity<>(
                    "There is no weather observation at this time", HttpStatus.NOT_FOUND);
        } catch (NoUsageAllowedException e) {
            throw new NoUsageAllowedException("Usage of selected vehicle type is forbidden");
        } catch (DateTimeParseException e) {
            throw new DateTimeParseException("Invalid datetime format", localDateTime, 0);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("No such vehicle type exist");
        }

        return new ResponseEntity<>("Delivery fee : " + totalFee, HttpStatus.OK);
    }

    private LocalDateTime converLocalDateTime(String stringDateTime) throws DateTimeParseException {
        if (stringDateTime.isEmpty())
            return null;
        return LocalDateTime.parse(
                stringDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
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
            throws NoUsageAllowedException, NullPointerException {

        WeatherObservationDTO weatherObservation;
        if (localDateTime == null)
            weatherObservation = weatherObservationService
                    .getLatestObservationByCityName(cityName);
        else
            weatherObservation = weatherObservationService
                    .getWeatherObservationByCityNameAndTimeOfObservation(cityName, localDateTime);
        double totalFee = 0.0;
        totalFee += calculateRBF(cityName, vehicleType);
        logger.info("RBF: " + totalFee);
        if (weatherObservation == null)
            throw new WeatherObservationNotFoundException("There are no any weather observation in target city!");
        totalFee += calculateExtraFee(weatherObservation, vehicleType);
        return totalFee;
    }

    private double calculateRBF(String cityName, VehicleType vehicleType) {
        RegionalBaseFee regionalBaseFee = getRegionalBaseFeeByCityNameAndVehicleType(cityName, vehicleType);
        logger.info("RGB" + "(" + cityName + ", " + vehicleType + "): " + regionalBaseFee.getBaseFeeValue());
        return regionalBaseFee.getBaseFeeValue();

    }

    private RegionalBaseFee getRegionalBaseFeeByCityNameAndVehicleType(String cityName, VehicleType vehicleType) {
        return regionalBaseFeeRepository.findTopByCityNameAndVehicleType(cityName, vehicleType);
    }

    private double calculateExtraFee (WeatherObservationDTO weatherObservation, VehicleType vehicleType)
            throws NoUsageAllowedException {

        double ATEF = calculateATEF(weatherObservation.airTemperature(), vehicleType);
        double WSEF = calculateWSEF(weatherObservation.windSpeed(), vehicleType);
        double WPEF = calculateWPEF(
                vehicleType, PhenomenonType.getPhenomenonType(weatherObservation.weatherPhenomenon())
        );
        logger.info(
                "\nATEF: " + ATEF + " (Air temperature: " + weatherObservation.airTemperature() + ")\n"
                + "WSEF: " + WSEF + " (Wind speed: " + weatherObservation.windSpeed() + ")\n"
                + "WPEF: " + WPEF + " (Phenomenon: " + weatherObservation.weatherPhenomenon() + ")\n"
                + "Time of observation: " + weatherObservation.timeOfObservation());

        return ATEF + WSEF + WPEF;
    }

    private double calculateATEF(double air, VehicleType vehicleType) {
        BusinessRuleDTO businessRuleDTO;
        businessRuleDTO = businessRuleService
                .getBusinessRuleByVehicleTypeAndWeatherConditionTypeAndRangeValue(
                        vehicleType, WeatherConditionType.ATEF, air);
        if (businessRuleDTO == null){
            return 0.0;
        }
        return businessRuleDTO.extraFeeValue();
    }

    private double calculateWSEF(double wind, VehicleType vehicleType) {
        BusinessRuleDTO businessRuleDTO;
        businessRuleDTO = businessRuleService
                .getBusinessRuleByVehicleTypeAndWeatherConditionTypeAndRangeValue(
                        vehicleType, WeatherConditionType.WSEF, wind);
        if (businessRuleDTO == null){
            return 0.0;
        }
        return businessRuleDTO.extraFeeValue();
    }

    private double calculateWPEF(VehicleType vehicleType, PhenomenonType phenomenonType) throws NoUsageAllowedException {
        if (phenomenonType == PhenomenonType.NOPE){
            return 0.0;
        }
        BusinessRuleDTO businessRuleDTO;
        businessRuleDTO = businessRuleService
                .getBusinessRuleByVehicleTypeAndPhenomenonType(vehicleType, phenomenonType);
        if (businessRuleDTO == null){
            return 0.0;
        }
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
        logger.info("Regional Base Fee was successfully updated!");
        return new ResponseEntity<>("Regional Base Fee was successfully updated!", HttpStatus.OK);
    }
}