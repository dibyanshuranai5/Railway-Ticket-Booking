package com.trainbooking.trainsearch.dto;

import java.util.List;

public record TrainResponse(
        String trainNumber,
        String trainName,
        String fromStation,
        String toStation,
        String departure,
        String arrival,
        int durationMinutes,
        List<TrainClassResponse> classes) {
}
