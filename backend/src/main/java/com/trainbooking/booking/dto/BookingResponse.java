package com.trainbooking.booking.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record BookingResponse(
        String id,
        String trainNumber,
        String trainName,
        String fromStation,
        String toStation,
        LocalDate journeyDate,
        String departure,
        String arrival,
        String classCode,
        List<PassengerResponse> passengers,
        BigDecimal fareINR,
        String status,
        String pnr) {
}
