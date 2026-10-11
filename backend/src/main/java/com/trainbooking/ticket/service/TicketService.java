package com.trainbooking.ticket.service;

import com.trainbooking.auth.model.User;
import com.trainbooking.auth.repository.UserRepository;
import com.trainbooking.booking.dto.PassengerResponse;
import com.trainbooking.pnr.repository.PnrRepository;
import com.trainbooking.ticket.dto.TicketResponse;
import com.trainbooking.ticket.model.Ticket;
import com.trainbooking.ticket.repository.TicketRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TicketService {
    private final PnrRepository pnrs;
    private final TicketRepository tickets;
    private final UserRepository users;

    public TicketService(PnrRepository pnrs, TicketRepository tickets, UserRepository users) {
        this.pnrs = pnrs;
        this.tickets = tickets;
        this.users = users;
    }

    public TicketResponse getVerifiedTicket(String rawPnr, String mobileLast4) {
        String pnr = rawPnr.trim().toUpperCase();
        var pnrRecord = pnrs.findById(pnr)
                .orElseThrow(TicketService::ticketNotFound);
        Ticket ticket = tickets.findById(pnrRecord.ticketId)
                .orElseThrow(TicketService::ticketNotFound);
        User account = users.findById(ticket.userId)
                .orElseThrow(TicketService::ticketNotFound);
        if (account.phone == null || !account.phone.endsWith(mobileLast4)) {
            throw ticketNotFound();
        }
        return toResponse(ticket);
    }

    public static TicketResponse toResponse(Ticket ticket) {
        List<PassengerResponse> passengers = ticket.passengers.stream()
                .map(passenger -> new PassengerResponse(
                        passenger.name,
                        passenger.age,
                        passenger.gender,
                        passenger.berthPreference,
                        passenger.seatNumber))
                .toList();
        return new TicketResponse(
                ticket.pnr,
                ticket.bookingId,
                ticket.trainNumber,
                ticket.trainName,
                ticket.fromStation,
                ticket.toStation,
                ticket.journeyDate,
                ticket.departure,
                ticket.arrival,
                ticket.classCode,
                passengers,
                ticket.fareINR,
                ticket.paymentStatus,
                ticket.bookingStatus);
    }

    private static ResponseStatusException ticketNotFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket or verification details not found.");
    }
}
