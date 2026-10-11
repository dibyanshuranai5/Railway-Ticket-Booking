package com.trainbooking.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(
        @NotBlank @Size(min = 2, max = 100) String name,
        @NotBlank @Pattern(regexp = "^\\+?[0-9]{10,15}$") String phone) {
}
