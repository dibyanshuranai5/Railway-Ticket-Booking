package com.trainbooking.booking.service;

import com.trainbooking.auth.model.User;
import com.trainbooking.booking.dto.BookingInput;
import com.trainbooking.booking.dto.BookingResponse;
import com.trainbooking.booking.dto.BookingSummaryResponse;
import com.trainbooking.booking.dto.PassengerInput;
import com.trainbooking.booking.dto.PassengerResponse;
import com.trainbooking.booking.model.Booking;
import com.trainbooking.booking.model.Passenger;
import com.trainbooking.booking.repository.BookingRepository;
import com.trainbooking.trainsearch.api.TrainDataProvider;
import com.trainbooking.trainsearch.dto.TrainClassResponse;
import com.trainbooking.trainsearch.dto.TrainResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BookingService {
    private final BookingRepository bookings;
    private final TrainDataProvider trainProvider;

    public BookingService(BookingRepository bookings, TrainDataProvider trainProvider) {
        this.bookings = bookings;
        this.trainProvider = trainProvider;
    }

    public BookingResponse create(User user, BookingInput input) {
        if (input.journeyDate() == null) {
            throw new IllegalArgumentException("Journey date is required.");
        }
        if (input.journeyDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Journey date cannot be in the past.");
        }

        // Fetch train, class availability and fare from the live provider; never trust a client fare.
        TrainResponse train = trainProvider.findTrain(input.trainNumber(), input.journeyDate());
        TrainClassResponse trainClass = train.classes().stream()
                .filter(candidate -> candidate.classCode().equalsIgnoreCase(input.classCode()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "That class is not available."));
        if (trainClass.availableSeats() < input.passengers().size()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "There are not enough seats in this class.");
        }

        Booking booking = new Booking();
        booking.userId = user.id;
        booking.contactPhone = user.phone;
        booking.trainNumber = train.trainNumber();
        booking.trainName = train.trainName();
        booking.fromStation = train.fromStation();
        booking.toStation = train.toStation();
        booking.journeyDate = input.journeyDate();
        booking.departure = train.departure();
        booking.arrival = train.arrival();
        booking.classCode = trainClass.classCode();
        booking.passengers = input.passengers().stream()
                .map(BookingService::toPassenger)
                .toList();
        booking.fareINR = trainClass.fareINR().multiply(BigDecimal.valueOf(input.passengers().size()));
        booking.status = "PENDING_PAYMENT";
        booking.createdAt = Instant.now();
        return toResponse(bookings.save(booking));
    }

    public List<BookingResponse> listForUser(String userId) {
        return bookings.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(BookingService::toResponse)
                .toList();
    }

    public Booking requireOwned(String bookingId, String userId) {
        return bookings.findByIdAndUserId(bookingId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found."));
    }

    public BookingSummaryResponse summary(String userId) {
        List<Booking> all = bookings.findByUserIdOrderByCreatedAtDesc(userId);
        int upcoming = (int) all.stream()
                .filter(booking -> "CONFIRMED".equals(booking.status))
                .filter(booking -> booking.journeyDate != null && !booking.journeyDate.isBefore(LocalDate.now()))
                .count();
        int previous = (int) all.stream()
                .filter(booking -> booking.journeyDate != null && booking.journeyDate.isBefore(LocalDate.now()))
                .count();
        return new BookingSummaryResponse(upcoming, previous, all.size());
    }

    public static BookingResponse toResponse(Booking booking) {
        List<PassengerResponse> passengers = booking.passengers.stream()
                .map(passenger -> new PassengerResponse(
                        passenger.name,
                        passenger.age,
                        passenger.gender,
                        passenger.berthPreference,
                        passenger.seatNumber))
                .toList();
        return new BookingResponse(
                booking.id,
                booking.trainNumber,
                booking.trainName,
                booking.fromStation,
                booking.toStation,
                booking.journeyDate,
                booking.departure,
                booking.arrival,
                booking.classCode,
                passengers,
                booking.fareINR,
                booking.status,
                booking.pnr);
    }

    private static Passenger toPassenger(PassengerInput input) {
        return new Passenger(
                input.name().trim(),
                input.age(),
                input.gender().trim().toUpperCase(),
                input.berthPreference(),
                null);
    }
}
