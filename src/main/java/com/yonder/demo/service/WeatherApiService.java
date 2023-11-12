package com.yonder.demo.service;

import com.yonder.demo.model.domain.WeatherData;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class WeatherApiService {

    public Mono<WeatherData> retrieveWeatherData(String cityName, String weatherApiUrl){

        return WebClient.builder().
                baseUrl(weatherApiUrl)
                .build()
                .get()
                .uri( cityName)
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        response -> Mono.empty())
                .bodyToMono(WeatherData.class)
                .onErrorResume(error -> Mono.just(new WeatherData()));
    }
}
