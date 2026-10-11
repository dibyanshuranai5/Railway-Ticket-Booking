package com.trainbooking.trainsearch.api;

public class TrainProviderUnavailableException extends RuntimeException {
    public TrainProviderUnavailableException(String message) {
        super(message);
    }
}
