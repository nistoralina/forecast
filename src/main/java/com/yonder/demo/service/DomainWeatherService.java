package com.yonder.demo.service;

import com.yonder.demo.config.ApplicationConfig;
import com.yonder.demo.exception.CustomException;
import com.yonder.demo.model.domain.Forecast;
import com.yonder.demo.model.dto.WeatherResponseDto;
import com.yonder.demo.util.CsvUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DomainWeatherService {

    private final ApplicationConfig applicationConfig;
    private final WeatherApiService weatherApiService;
    private final CsvUtil csvUtil;

    public DomainWeatherService(ApplicationConfig applicationConfig, WeatherApiService weatherApiService, CsvUtil csvUtil) {
        this.applicationConfig = applicationConfig;
        this.weatherApiService = weatherApiService;
        this.csvUtil = csvUtil;
    }

    public Flux<WeatherResponseDto> getWeatherResponseData(List<String> cities) {

        var filteredCities = getSanitizedCities(cities);

        // Request weather data retrieval from API for each allowed city
        // Calculate the average values and return a Flux of responses
        Flux<WeatherResponseDto> responseDtoFlux = Flux.fromIterable(filteredCities)
                .flatMapSequential(city -> weatherApiService.retrieveWeatherDataFromApi(city, applicationConfig.getWeatherApiUrl())
                        .flatMap(weatherData -> {
                            if (weatherData.getForecast() == null || weatherData.getForecast().isEmpty()) {
                                return Mono.just(new WeatherResponseDto(city, "", ""));
                            } else {
                                var responseDto = calculateAverage(city, weatherData.getForecast());
                                return Mono.just(responseDto);
                            }
                        })
                        .onErrorResume(Exception.class, e ->
                                Mono.error(new CustomException("There was an unexpected error.", HttpStatus.INTERNAL_SERVER_ERROR))
                        )
                );
        csvUtil.writeToCsvFlux(responseDtoFlux, applicationConfig.getCsvFilePath());
        return responseDtoFlux;
    }

    private List<String> getSanitizedCities(List<String> inputCities) {

        //Filter the cities that are not allowed to be displayed, sorts the distinct cities
        return inputCities.stream()
                .filter(applicationConfig.getAllowedCities()::contains)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    private WeatherResponseDto calculateAverage(String city, List<Forecast> weatherForecast) {
        // Calculate the average temperature and wind from the forecast data
        Double totalTemperature = 0.0;
        Double totalWind = 0.0;

        for (Forecast forecast : weatherForecast) {
            if ((forecast.getTemperature() != null) && (forecast.getWind() != null)) {

                totalTemperature += forecast.getTemperature();
                totalWind += forecast.getWind();
            }
        }

        Double averageTemperature = weatherForecast.isEmpty() ? 0.0 : totalTemperature / weatherForecast.size();
        Double averageWind = weatherForecast.isEmpty() ? 0.0 : totalWind / weatherForecast.size();

        return new WeatherResponseDto(city, averageTemperature.toString(), averageWind.toString());
    }
}
