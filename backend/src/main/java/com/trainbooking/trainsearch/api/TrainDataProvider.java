package com.trainbooking.trainsearch.api;

import com.trainbooking.runtime.dto.TrainRuntimeStatusResponse;
import com.trainbooking.trainsearch.dto.TrainResponse;
import java.time.LocalDate;
import java.util.List;

/**
 * Provider boundary for live railway data. Add a provider-specific implementation
 * here only after its API and authorization have been supplied.
 */
public interface TrainDataProvider {
    boolean isConnected();
    String providerName();
    List<TrainResponse> search(String fromStation, String toStation, LocalDate journeyDate);
    TrainResponse findTrain(String trainNumber, LocalDate journeyDate);
    TrainRuntimeStatusResponse runtimeStatus(String trainNumber, LocalDate journeyDate);
}
