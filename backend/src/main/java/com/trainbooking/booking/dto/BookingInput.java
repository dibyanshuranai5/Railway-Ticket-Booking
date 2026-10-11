package com.trainbooking.booking.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public record BookingInput(
        @NotBlank String trainNumber,
        @NotBlank String classCode,
        LocalDate journeyDate,
        @NotEmpty @Size(max = 6) List<@Valid PassengerInput> passengers) {
}
