package com.trainbooking.trainsearch.dto;

import java.util.List;

public record ProviderStatusResponse(
        boolean connected,
        String provider,
        String message,
        List<String> capabilities) {
}
