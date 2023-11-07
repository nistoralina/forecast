package com.yonder.demo.model.domain;

import lombok.Data;

@Data
public class Forecast {

    private String day;
    private Double temperature;
    private Double wind;
}
