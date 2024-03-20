package com.delivery.deliveryfee.weather_observations;

import com.delivery.deliveryfee.station_city_mapping.StationCityMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WeatherObservationService {

    private final WeatherObservationRepository weatherObservationRepository;
    private final WeatherObservationDTOMapper weatherObservationDTOMapper;
    private final StationCityMappingService stationCityMappingService;
    private static final String url = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";

    @Autowired
    public WeatherObservationService(WeatherObservationRepository weatherObservationRepository,
                                     WeatherObservationDTOMapper weatherObservationDTOMapper,
                                     StationCityMappingService stationCityMappingService) {
        this.weatherObservationRepository = weatherObservationRepository;
        this.weatherObservationDTOMapper = weatherObservationDTOMapper;
        this.stationCityMappingService = stationCityMappingService;
    }

    /**
     * Gets all weather observations from database using repository
     *
     * @return List of WeatherObservation objects
     */
    public List<WeatherObservationDTO> getAllWeatherObservations() {
        return weatherObservationRepository.findAll()
                .stream()
                .map(weatherObservationDTOMapper)
                .collect(Collectors.toList());
    }

    /**
     * Gets the latest weather observation from database using repository
     *
     * @return List of WeatherObservation objects
     */

    public List<WeatherObservationDTO> getLatestWeatherObservations() {
        Optional<List<WeatherObservation>> optionalWeatherObservations =  weatherObservationRepository
                .findTop3ByOrderByTimeOfObservationDesc();
        return optionalWeatherObservations.map(weatherObservations -> weatherObservations.stream()
                .map(weatherObservationDTOMapper)
                .collect(Collectors.toList())).orElse(null);
    }


    /**
     * Method, which gets all observations of city name
     *
     * @param cityName name of city
     * @return list of WeatherObservation objects
     */
    public List<WeatherObservationDTO> getObservationsByCityName(String cityName) {
        String stationName = stationCityMappingService.getStationNameByCityName(cityName);
        Optional<List<WeatherObservation>> optionalWeatherObservations =  weatherObservationRepository
                .findWeatherObservationsByStationName(stationName);
        return optionalWeatherObservations.map(weatherObservations -> weatherObservations.stream()
                .map(weatherObservationDTOMapper)
                .collect(Collectors.toList())).orElse(null);
    }

    /**
     * Method, which gets all observations of city name
     *
     * @param cityName name of city
     * @return list of WeatherObservation objects
     */
    public WeatherObservationDTO getLatestObservationByCityName(String cityName) {
        String stationName = stationCityMappingService.getStationNameByCityName(cityName);
        return weatherObservationRepository.findTopByStationNameOrderByTimeOfObservationDesc(stationName)
                .map(weatherObservationDTOMapper)
                .orElseThrow();
    }

    /**
     * Gets weather observation by city name and observation time
     *
     * @param cityName name of target city
     * @param localDateTime datetime of observation
     * @return weather observation that is actual for observation time
     */
    public WeatherObservationDTO getWeatherObservationByCityNameAndTimeOfObservation(
            String cityName, LocalDateTime localDateTime
    ) {
        String stationName = stationCityMappingService.getStationNameByCityName(cityName);
        return weatherObservationRepository
                .findTopByStationNameAndTimeOfObservationIsBeforeOrderByTimeOfObservationDesc(
                        stationName, localDateTime)
                .map(weatherObservationDTOMapper).orElseThrow();
    }

    /**
     * Method, which saves WeatherObservation object to database
     *
     * @param weatherObservation object to save
     */
    public void saveWeatherObservation(WeatherObservation weatherObservation) {
        weatherObservationRepository.save(weatherObservation);
    }

    /**
     * Method, which saves list of WeatherObservation objects to database
     *
     * @param weatherObservations list of objects to save
     */
    public void saveWeatherObservations(List<WeatherObservation> weatherObservations) {
        weatherObservationRepository.saveAll(weatherObservations);
    }


    /**
     * Method, which processes weather observations data
     * Automatically every hour at HH:15:00
     */
    @Scheduled(cron = "0 15 * * * ?")
    public void updateWeatherData() {

        try {
            NodeList stationList = fetchStationDataFromXML();
            List<WeatherObservation> weatherObservations = extractValidObservations(stationList);
            saveWeatherObservations(weatherObservations);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private NodeList fetchStationDataFromXML() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new URL(url).openStream());
        doc.getDocumentElement().normalize();
        return doc.getElementsByTagName("station");
    }

    private List<WeatherObservation> extractValidObservations(NodeList stationList) {
        List<WeatherObservation> observations = new ArrayList<>();
        for (int index = 0; index < stationList.getLength(); index++) {
            Node node = stationList.item(index);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String name = element.getElementsByTagName("name").item(0).getTextContent();
                if (isValidStation(name)) {
                    WeatherObservation observation = createObservationFromElement(element);
                    observations.add(observation);
                }
            }
        }
        return observations;
    }

    private WeatherObservation createObservationFromElement(Element element) {

        String name = element.getElementsByTagName("name").item(0).getTextContent();
        String wmoCode = element.getElementsByTagName("wmocode").item(0).getTextContent();
        String airTemperature = element.getElementsByTagName("airtemperature").item(0).getTextContent();
        String windSpeed = element.getElementsByTagName("windspeed").item(0).getTextContent();
        if (windSpeed.isEmpty())
                windSpeed = "0.0";
        String phenomenon = element.getElementsByTagName("phenomenon").item(0).getTextContent();
        LocalDateTime localDateTime = LocalDateTime.now();

        System.out.println(name + " " + wmoCode + " " + airTemperature + " " + windSpeed + " " + phenomenon);

        return new WeatherObservation(name, wmoCode, Double.parseDouble(airTemperature),
                Double.parseDouble(windSpeed), phenomenon, localDateTime);
    }

    private boolean isValidStation(String stationName) {
        return stationCityMappingService.existsStationName(stationName);
    }

}
