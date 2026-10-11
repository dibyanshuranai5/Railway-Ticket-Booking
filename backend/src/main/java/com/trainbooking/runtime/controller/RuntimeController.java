package com.trainbooking.runtime.controller;

import com.trainbooking.runtime.dto.TrainRuntimeStatusResponse;
import com.trainbooking.runtime.service.RuntimeService;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trains")
public class RuntimeController {
    private final RuntimeService runtimeService;

    public RuntimeController(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    @GetMapping("/{trainNumber}/status")
    public TrainRuntimeStatusResponse getStatus(
            @PathVariable String trainNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate journeyDate) {
        return runtimeService.getStatus(trainNumber, journeyDate);
    }
}
