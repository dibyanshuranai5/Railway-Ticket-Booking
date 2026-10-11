package com.trainbooking.payment.dto;

import java.math.BigDecimal;

public record PaymentResultResponse(
        String id,
        String bookingId,
        BigDecimal amountINR,
        String status,
        String pnr) {
}
