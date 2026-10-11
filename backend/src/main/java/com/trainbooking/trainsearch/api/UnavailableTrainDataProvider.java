package com.trainbooking.trainsearch.api;

import com.trainbooking.runtime.dto.TrainRuntimeStatusResponse;
import com.trainbooking.trainsearch.dto.TrainResponse;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Honest default until the app owner connects a real railway API provider.
 * This adapter intentionally returns no fabricated timetable or status data.
 */
@Component
public class UnavailableTrainDataProvider implements TrainDataProvider {
    public static final String MESSAGE =
            "Live train search and running status are paused until a railway data provider is connected.";

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public String providerName() {
        return null;
    }

    @Override
    public List<TrainResponse> search(String fromStation, String toStation, LocalDate journeyDate) {
        throw new TrainProviderUnavailableException(MESSAGE);
    }

    @Override
    public TrainResponse findTrain(String trainNumber, LocalDate journeyDate) {
        throw new TrainProviderUnavailableException(MESSAGE);
    }

    @Override
    public TrainRuntimeStatusResponse runtimeStatus(String trainNumber, LocalDate journeyDate) {
        throw new TrainProviderUnavailableException(MESSAGE);
    }
}
