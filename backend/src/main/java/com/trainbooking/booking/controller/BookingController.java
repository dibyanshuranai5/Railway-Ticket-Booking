package com.trainbooking.booking.controller;

import com.trainbooking.auth.model.User;
import com.trainbooking.auth.service.AuthService;
import com.trainbooking.booking.dto.BookingInput;
import com.trainbooking.booking.dto.BookingResponse;
import com.trainbooking.booking.service.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class BookingController {
    private final AuthService authService;
    private final BookingService bookingService;

    public BookingController(AuthService authService, BookingService bookingService) {
        this.authService = authService;
        this.bookingService = bookingService;
    }

    @PostMapping("/bookings")
    public ResponseEntity<BookingResponse> create(
            @Valid @RequestBody BookingInput input,
            HttpServletRequest request) {
        User user = authService.requireUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.create(user, input));
    }

    @GetMapping("/users/me/bookings")
    public List<BookingResponse> list(HttpServletRequest request) {
        User user = authService.requireUser(request);
        return bookingService.listForUser(user.id);
    }
}
