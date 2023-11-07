package com.yonder.demo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
@Configuration
@ConfigurationProperties
@Getter
@Setter
public class ApplicationConfig {

    private String weatherApiUrl;
    private List<String> allowedCities;
}
