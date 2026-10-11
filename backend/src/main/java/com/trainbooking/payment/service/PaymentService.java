package com.trainbooking.payment.service;

import com.trainbooking.auth.model.User;
import com.trainbooking.booking.model.Booking;
import com.trainbooking.booking.model.Passenger;
import com.trainbooking.booking.repository.BookingRepository;
import com.trainbooking.config.MongoIndexInitializer;
import com.trainbooking.payment.dto.PaymentResultResponse;
import com.trainbooking.payment.dto.SandboxPaymentRequest;
import com.trainbooking.payment.model.Payment;
import com.trainbooking.payment.repository.PaymentRepository;
import com.trainbooking.pnr.model.PnrRecord;
import com.trainbooking.pnr.repository.PnrRepository;
import com.trainbooking.ticket.model.Ticket;
import com.trainbooking.ticket.repository.TicketRepository;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PaymentService {
    private static final SecureRandom RANDOM = new SecureRandom();

    private final BookingRepository bookings;
    private final PaymentRepository payments;
    private final TicketRepository tickets;
    private final PnrRepository pnrs;
    private final MongoIndexInitializer indexInitializer;

    public PaymentService(
            BookingRepository bookings,
            PaymentRepository payments,
            TicketRepository tickets,
            PnrRepository pnrs,
            MongoIndexInitializer indexInitializer) {
        this.bookings = bookings;
        this.payments = payments;
        this.tickets = tickets;
        this.pnrs = pnrs;
        this.indexInitializer = indexInitializer;
    }

    public PaymentResultResponse pay(String userId, String bookingId, SandboxPaymentRequest input) {
        Booking booking = bookings.findByIdAndUserId(bookingId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found."));

        if ("CONFIRMED".equals(booking.status)) {
            return payments.findFirstByBookingIdAndStatus(booking.id, "SUCCESS")
                    .map(payment -> toResponse(payment, booking.pnr))
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.CONFLICT, "This booking is already confirmed."));
        }
        if (!"PENDING_PAYMENT".equals(booking.status)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This booking is not awaiting payment.");
        }
        indexInitializer.ensureIndexes();

        Payment payment = new Payment();
        payment.bookingId = booking.id;
        payment.userId = userId;
        payment.amountINR = booking.fareINR;
        payment.createdAt = Instant.now();

        if ("DECLINE".equals(input.decision())) {
            payment.status = "FAILED";
            payment = payments.save(payment);
            return toResponse(payment, null);
        }

        String pnr = createUniquePnr();
        payment.status = "SUCCESS";
        payment.pnr = pnr;

        Ticket ticket = ticketFromBooking(booking, pnr);
        Ticket savedTicket = tickets.save(ticket);
        pnrs.save(new PnrRecord(pnr, savedTicket.id, booking.id));
        booking.status = "CONFIRMED";
        booking.pnr = pnr;
        bookings.save(booking);
        payment = payments.save(payment);
        return toResponse(payment, pnr);
    }

    private String createUniquePnr() {
        for (int attempt = 0; attempt < 20; attempt++) {
            StringBuilder candidate = new StringBuilder("4");
            for (int i = 1; i < 10; i++) {
                candidate.append(RANDOM.nextInt(10));
            }
            String pnr = candidate.toString();
            if (!tickets.existsByPnr(pnr) && !pnrs.existsById(pnr)) {
                return pnr;
            }
        }
        throw new IllegalStateException("Could not generate a unique PNR. Please retry payment.");
    }

    private static Ticket ticketFromBooking(Booking booking, String pnr) {
        Ticket ticket = new Ticket();
        ticket.pnr = pnr;
        ticket.bookingId = booking.id;
        ticket.userId = booking.userId;
        ticket.trainNumber = booking.trainNumber;
        ticket.trainName = booking.trainName;
        ticket.fromStation = booking.fromStation;
        ticket.toStation = booking.toStation;
        ticket.journeyDate = booking.journeyDate;
        ticket.departure = booking.departure;
        ticket.arrival = booking.arrival;
        ticket.classCode = booking.classCode;
        ticket.passengers = booking.passengers.stream()
                .map(PaymentService::copyPassenger)
                .toList();
        ticket.fareINR = booking.fareINR;
        ticket.paymentStatus = "PAID";
        ticket.bookingStatus = "CONFIRMED";
        ticket.issuedAt = Instant.now();
        return ticket;
    }

    private static Passenger copyPassenger(Passenger source) {
        return new Passenger(source.name, source.age, source.gender, source.berthPreference, source.seatNumber);
    }

    private static PaymentResultResponse toResponse(Payment payment, String pnr) {
        return new PaymentResultResponse(
                payment.id,
                payment.bookingId,
                payment.amountINR,
                payment.status,
                pnr);
    }
}
