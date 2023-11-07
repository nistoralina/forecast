package com.yonder.demo.controller;

import com.yonder.demo.model.dto.WeatherResponseDto;
import com.yonder.demo.service.DomainWeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

   private DomainWeatherService domainWeatherService;

    public WeatherController(DomainWeatherService domainWeatherService) {
        this.domainWeatherService = domainWeatherService;
    }

    @GetMapping()
    public Flux<WeatherResponseDto> getWeatherForecastAverage(@RequestParam(name = "city") List<String> cities){

        Flux<WeatherResponseDto> weatherResponse = domainWeatherService.getWeatherResponseData(cities);
        return weatherResponse;
    }
}
