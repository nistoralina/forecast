package com.yonder.demo.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherData {

    private Double temperature;
    private Double wind;
    private String description;
    private List<Forecast> forecast;
}
