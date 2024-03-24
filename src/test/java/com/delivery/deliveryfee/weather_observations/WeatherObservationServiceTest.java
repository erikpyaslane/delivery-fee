package com.delivery.deliveryfee.weather_observations;

import com.delivery.deliveryfee.exceptions.WeatherObservationNotFoundException;
import com.delivery.deliveryfee.station_city_mapping.StationCityMappingRepository;
import com.delivery.deliveryfee.station_city_mapping.StationCityMappingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class WeatherObservationServiceTest {

    @Mock
    private WeatherObservationRepository weatherObservationRepository;

    @Mock
    private WeatherObservationDTOMapper weatherObservationDTOMapper;

    @Mock
    private StationCityMappingService stationCityMappingService;

    @InjectMocks
    private WeatherObservationService weatherObservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getLatestObservationByCityName_ValidCity_ReturnsWeatherObservationDTO() {
        // Arrange
        String cityName = "Tartu";
        String stationName = "Tartu-Tõravere";
        LocalDateTime now = LocalDateTime.now();
        WeatherObservation observation = new WeatherObservation(stationName, "26242", 5.0, 5.0, "", now);
        WeatherObservationDTO expectedDTO = new WeatherObservationDTO(1L, stationName, "26242", 5.0, 5.0, "", now);
        when(stationCityMappingService.getStationNameByCityName(cityName)).thenReturn(stationName);
        when(weatherObservationRepository.findTopByStationNameOrderByTimeOfObservationDesc(stationName))
                .thenReturn(Optional.of(observation));
        when(weatherObservationDTOMapper.apply(observation)).thenReturn(expectedDTO);

        // Act
        WeatherObservationDTO result = weatherObservationService.getLatestObservationByCityName(cityName);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTO, result);
    }

    @Test
    void getAllWeatherObservations_ReturnsListOfWeatherObservationDTOs() {
        // Arrange
        List<WeatherObservation> observations = createWeatherObservations(2);
        List<WeatherObservationDTO> expectedDTOs = createWeatherObservationDTOs(observations);
        when(weatherObservationRepository.findAll()).thenReturn(observations);
        when(weatherObservationDTOMapper.apply(any(WeatherObservation.class))).thenReturn(expectedDTOs.get(0), expectedDTOs.get(1));

        // Act
        List<WeatherObservationDTO> result = weatherObservationService.getAllWeatherObservations();

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTOs, result);
    }

    @Test
    void getLatestWeatherObservations_ReturnsListOfWeatherObservationDTOs() {
        // Arrange
        List<WeatherObservation> observations = createWeatherObservations(2);
        List<WeatherObservationDTO> expectedDTOs = createWeatherObservationDTOs(observations);
        when(weatherObservationRepository.findTop3ByOrderByTimeOfObservationDesc()).thenReturn(Optional.of(observations));
        when(weatherObservationDTOMapper.apply(any(WeatherObservation.class))).thenReturn(expectedDTOs.get(0), expectedDTOs.get(1));

        // Act
        List<WeatherObservationDTO> result = weatherObservationService.getLatestWeatherObservations();

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTOs, result);
    }

    @Test
    void getObservationsByCityName_ReturnsListOfWeatherObservationDTOs() {
        // Arrange
        String cityName = "Tartu";
        String stationName = "Tartu-Tõravere";
        List<WeatherObservation> observations = createWeatherObservations(2);
        List<WeatherObservationDTO> expectedDTOs = createWeatherObservationDTOs(observations);
        when(stationCityMappingService.getStationNameByCityName(cityName)).thenReturn(stationName);
        when(weatherObservationRepository.findWeatherObservationsByStationName(stationName)).thenReturn(Optional.of(observations));
        when(weatherObservationDTOMapper.apply(any(WeatherObservation.class))).thenReturn(expectedDTOs.get(0), expectedDTOs.get(1));

        // Act
        List<WeatherObservationDTO> result = weatherObservationService.getObservationsByCityName(cityName);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTOs, result);
    }

    private List<WeatherObservation> createWeatherObservations(int count) {
        List<WeatherObservation> observations = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            observations.add(new WeatherObservation("Station" + (i + 1), "12345",
                    10.0, 20.0, "Mist", LocalDateTime.now()));
        }
        return observations;
    }

    private List<WeatherObservationDTO> createWeatherObservationDTOs(List<WeatherObservation> observations) {
        List<WeatherObservationDTO> dtos = new ArrayList<>();
        for (WeatherObservation observation : observations) {
            dtos.add(weatherObservationDTOMapper.apply(observation));
        }
        return dtos;
    }
}