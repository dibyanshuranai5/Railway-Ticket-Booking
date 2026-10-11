package com.trainbooking.ticket.dto;

import com.trainbooking.booking.dto.PassengerResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record TicketResponse(
        String pnr,
        String bookingId,
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
        String paymentStatus,
        String bookingStatus) {
}
