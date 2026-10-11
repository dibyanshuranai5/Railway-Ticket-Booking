package com.trainbooking.user.controller;

import com.trainbooking.auth.dto.ProfileUpdateRequest;
import com.trainbooking.auth.dto.UserProfileResponse;
import com.trainbooking.auth.model.User;
import com.trainbooking.auth.service.AuthService;
import com.trainbooking.booking.dto.BookingSummaryResponse;
import com.trainbooking.booking.service.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/me")
public class UserController {
    private final AuthService authService;
    private final BookingService bookingService;

    public UserController(AuthService authService, BookingService bookingService) {
        this.authService = authService;
        this.bookingService = bookingService;
    }

    @GetMapping
    public UserProfileResponse profile(HttpServletRequest request) {
        return authService.requireProfile(request);
    }

    @PatchMapping
    public UserProfileResponse update(
            @Valid @RequestBody ProfileUpdateRequest input,
            HttpServletRequest request) {
        return authService.updateProfile(request, input);
    }

    @GetMapping("/summary")
    public BookingSummaryResponse summary(HttpServletRequest request) {
        User user = authService.requireUser(request);
        return bookingService.summary(user.id);
    }
}
