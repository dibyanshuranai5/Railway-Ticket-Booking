package com.trainbooking.trainsearch.dto;

import java.math.BigDecimal;

public record TrainClassResponse(String classCode, int availableSeats, BigDecimal fareINR) {
}
