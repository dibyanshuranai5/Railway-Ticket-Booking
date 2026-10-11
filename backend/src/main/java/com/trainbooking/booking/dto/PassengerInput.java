package com.trainbooking.booking.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PassengerInput(
        @NotBlank @Size(min = 2, max = 100) String name,
        @Min(1) @Max(120) int age,
        @NotBlank String gender,
        String berthPreference) {
}
