package com.yonder.demo.service;

import com.yonder.demo.exception.CustomException;
import com.yonder.demo.model.domain.WeatherData;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class WeatherApiService {

    public Mono<WeatherData> retrieveWeatherDataFromApi(String cityName, String weatherApiUrl) {
        // Retrieve weather data from API
        // If no data is found for a city, an empty WeatherData object is returned
        return WebClient.builder().
                baseUrl(weatherApiUrl)
                .build()
                .get()
                .uri(cityName)
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        response -> Mono.empty())
                .bodyToMono(WeatherData.class)
                .onErrorResume(Exception.class, e ->
                        Mono.error(new CustomException("There was an unexpected error.", HttpStatus.INTERNAL_SERVER_ERROR))
                );
    }
}
