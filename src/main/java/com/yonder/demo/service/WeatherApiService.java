package com.yonder.demo.service;

import com.yonder.demo.model.domain.WeatherData;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class WeatherApiService {

    public Flux<WeatherData> retrieveWeatherDataList(List<String> cities, String weatherApiUrl){

        return Flux.fromIterable(cities)
                .flatMap(city -> retrieveWeatherData(city, weatherApiUrl));
    }

    public Mono<WeatherData> retrieveWeatherData(String cityName, String weatherApiUrl){

        return WebClient.builder().
                baseUrl(weatherApiUrl)
                .build()
                .get()
                .uri( cityName)
                .retrieve()
                .bodyToMono(WeatherData.class);
    }
}
