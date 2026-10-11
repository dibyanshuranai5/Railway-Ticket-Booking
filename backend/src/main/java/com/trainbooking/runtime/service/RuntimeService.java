package com.trainbooking.runtime.service;

import com.trainbooking.runtime.dto.TrainRuntimeStatusResponse;
import com.trainbooking.trainsearch.api.TrainDataProvider;
import java.time.LocalDate;
import org.springframework.stereotype.Service;

@Service
public class RuntimeService {
    private final TrainDataProvider provider;

    public RuntimeService(TrainDataProvider provider) {
        this.provider = provider;
    }

    public TrainRuntimeStatusResponse getStatus(String trainNumber, LocalDate journeyDate) {
        return provider.runtimeStatus(trainNumber.trim(), journeyDate);
    }
}
