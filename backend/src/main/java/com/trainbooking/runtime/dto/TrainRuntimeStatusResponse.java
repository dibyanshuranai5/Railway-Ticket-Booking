package com.trainbooking.runtime.dto;

import java.time.Instant;

public record TrainRuntimeStatusResponse(
        String trainNumber,
        String currentLocation,
        int delayMinutes,
        String expectedArrival,
        String nextStation,
        boolean isRunning,
        Instant updatedAt) {
}
