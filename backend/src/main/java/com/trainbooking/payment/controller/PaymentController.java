package com.trainbooking.payment.controller;

import com.trainbooking.auth.model.User;
import com.trainbooking.auth.service.AuthService;
import com.trainbooking.payment.dto.PaymentResultResponse;
import com.trainbooking.payment.dto.SandboxPaymentRequest;
import com.trainbooking.payment.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookings")
public class PaymentController {
    private final AuthService authService;
    private final PaymentService paymentService;

    public PaymentController(AuthService authService, PaymentService paymentService) {
        this.authService = authService;
        this.paymentService = paymentService;
    }

    @PostMapping("/{bookingId}/payments/sandbox")
    public PaymentResultResponse pay(
            @PathVariable String bookingId,
            @Valid @RequestBody SandboxPaymentRequest input,
            HttpServletRequest request) {
        User user = authService.requireUser(request);
        return paymentService.pay(user.id, bookingId, input);
    }
}
