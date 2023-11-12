package com.yonder.demo.util;

import com.yonder.demo.model.dto.WeatherResponseDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Getter
@Setter
@Component
public class CsvUtil {

    private final String userHome;
    // Retrieve user's home path
    public CsvUtil(@Value("${user.home}") String userHome) {
        this.userHome = userHome + File.separator;
    }

    public void writeToCsvFlux(Flux<WeatherResponseDto> responseDtoFlux, String csvFilePath) {
        // Use flatMap to asynchronously write all entries to the CSV file
        Mono<Void> writeCsvOperation = responseDtoFlux
                .collectList()
                .flatMap(entries -> Mono.fromRunnable(() -> writeToCsvFlux(entries, csvFilePath)))
                .then();

        // Subscribe to the reactive operation
        writeCsvOperation.subscribe(
                success -> System.out.println("CSV file created successfully!"),
                error -> error.printStackTrace()
        );
    }

    private void writeToCsvFlux(List<WeatherResponseDto> weatherResponseDtos, String csvFilePath) {
        //Create a csv file in the user home path
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userHome + csvFilePath))) {
            // Write headers to CSV
            writer.write("Name, Temperature, Wind");
            writer.newLine();

            // Format and write each entry to CSV
            for (WeatherResponseDto weatherResponseDto : weatherResponseDtos) {
                String csvData = String.format("%s, %s, %s",
                        weatherResponseDto.getName(),
                        weatherResponseDto.getTemperature(),
                        weatherResponseDto.getWind());

                // Write data to CSV
                writer.write(csvData);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing to CSV", e);
        }
    }
}
