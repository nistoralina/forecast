package com.yonder.demo.util;

import com.yonder.demo.model.dto.WeatherResponseDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Getter
@Setter
@Component
public class CsvUtil {

    public void writeResponseToCsv(String weatherResponseDtos, String csvFilePath) {
        try (
                BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath))) {
            // Write CSV header
            writer.write("Name, temperature, wind");
            writer.newLine();

            // Write data for each city
            writer.write(weatherResponseDtos);
            writer.newLine();

//            // Write data for each city
//            for (WeatherResponseDto weatherResponseDto : weatherResponseDtos) {
//                writer.write(String.format("%s, %.2f, %.2f",
//                        weatherResponseDto.getName(),
//                        weatherResponseDto.getTemperature(),
//                        weatherResponseDto.getWind()));
//                writer.newLine();
//            }

            System.out.println("CSV file created successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToCsv(Flux<WeatherResponseDto> responseDtoFlux, String csvFilePath) {
        // Use flatMap to asynchronously write all entries to the CSV file
        Mono<Void> writeCsvOperation = responseDtoFlux
                .collectList()
                .flatMap(entries -> Mono.fromRunnable(() -> writeToCsv(entries, csvFilePath)))
                .then();

        // Subscribe to the reactive operation
        writeCsvOperation.subscribe(
                success -> System.out.println("CSV file created successfully!"),
                error -> error.printStackTrace()
        );
    }

    private static void writeToCsv(List<WeatherResponseDto> weatherResponseDtos, String csvFilePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath))) {
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
