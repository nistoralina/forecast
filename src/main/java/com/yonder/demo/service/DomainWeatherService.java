package com.yonder.demo.service;

import com.yonder.demo.config.ApplicationConfig;
import com.yonder.demo.model.domain.Forecast;
import com.yonder.demo.model.dto.WeatherResponseDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DomainWeatherService {

    private ApplicationConfig applicationConfig;
    private WeatherApiService weatherApiService;

    public DomainWeatherService(ApplicationConfig applicationConfig, WeatherApiService weatherApiService) {
        this.applicationConfig = applicationConfig;
        this.weatherApiService = weatherApiService;
    }

    public Flux<WeatherResponseDto> getWeatherResponseData(List<String> cities) {

        var filteredCities = getSanitizedCities(cities);
        var weatherDataUrl = applicationConfig.getWeatherApiUrl();
//TODO: Daca nu exista data pentru orasul cautat serverul va returna 404 si valorile pentru media temperaturii si a vantului vor ramane empty
   //TODO:CSV: cu orasele ordonate alfabetic si media temperaturii si a vantului (fisierul o sa fie suprascris cu fiecare apel al API-ului)

        Flux<WeatherResponseDto> weatherDataResponse =Flux.zip(
                weatherApiService.retrieveWeatherDataList(filteredCities, weatherDataUrl),
                Flux.fromIterable(filteredCities),
                (weatherData , city) -> {
                    var weatherResponseDto = calculateAverage(city, weatherData.getForecast());
                    return weatherResponseDto;
                });

        return weatherDataResponse;
    }


    private List<String> getSanitizedCities(List<String> inputCities) {
        //Filter the cities that are not allowed to be displayed, sorts the cities

        var filteredCities = inputCities.stream()
                .filter(applicationConfig.getAllowedCities()::contains)
                .sorted()
                .collect(Collectors.toList());

        return filteredCities;
    }

    private WeatherResponseDto calculateAverage(String city, List<Forecast> weatherForecast) {

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

        return new WeatherResponseDto(city, averageTemperature, averageWind);
    }
}
