package com.yonder.demo.model.domain;

import lombok.Data;

import java.util.List;

@Data
public class WeatherData {

    private Double temperature;
    private Double wind;
    private String description;
    private List<Forecast> forecast;
}
