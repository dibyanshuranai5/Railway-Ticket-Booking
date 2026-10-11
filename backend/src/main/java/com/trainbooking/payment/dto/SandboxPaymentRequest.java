package com.trainbooking.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SandboxPaymentRequest(
        @NotBlank @Pattern(regexp = "APPROVE|DECLINE") String decision) {
}
