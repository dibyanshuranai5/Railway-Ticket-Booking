package com.trainbooking.trainsearch.service;

import com.trainbooking.trainsearch.api.TrainDataProvider;
import com.trainbooking.trainsearch.api.UnavailableTrainDataProvider;
import com.trainbooking.trainsearch.dto.ProviderStatusResponse;
import com.trainbooking.trainsearch.dto.TrainResponse;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TrainSearchService {
    private final TrainDataProvider provider;

    public TrainSearchService(TrainDataProvider provider) {
        this.provider = provider;
    }

    public ProviderStatusResponse providerStatus() {
        boolean connected = provider.isConnected();
        return new ProviderStatusResponse(
                connected,
                provider.providerName(),
                connected ? "Live railway information is connected." : UnavailableTrainDataProvider.MESSAGE,
                connected
                        ? List.of("search", "availability", "fare", "runtime-status")
                        : List.of());
    }

    public List<TrainResponse> search(String from, String to, LocalDate date) {
        if (from.equalsIgnoreCase(to)) {
            throw new IllegalArgumentException("Choose two different stations.");
        }
        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Journey date cannot be in the past.");
        }
        return provider.search(from.trim(), to.trim(), date);
    }
}
