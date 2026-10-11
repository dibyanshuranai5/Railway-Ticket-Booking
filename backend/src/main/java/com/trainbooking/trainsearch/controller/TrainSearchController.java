package com.trainbooking.trainsearch.controller;

import com.trainbooking.trainsearch.dto.ProviderStatusResponse;
import com.trainbooking.trainsearch.dto.TrainResponse;
import com.trainbooking.trainsearch.service.TrainSearchService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping
public class TrainSearchController {
    private final TrainSearchService trainSearchService;

    public TrainSearchController(TrainSearchService trainSearchService) {
        this.trainSearchService = trainSearchService;
    }

    @GetMapping("/railway/provider")
    public ProviderStatusResponse providerStatus() {
        return trainSearchService.providerStatus();
    }

    @GetMapping("/trains/search")
    public List<TrainResponse> search(
            @RequestParam @NotBlank @Size(max = 80) String fromStation,
            @RequestParam @NotBlank @Size(max = 80) String toStation,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate journeyDate) {
        return trainSearchService.search(fromStation, toStation, journeyDate);
    }
}
