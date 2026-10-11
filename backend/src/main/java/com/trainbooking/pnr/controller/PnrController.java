package com.trainbooking.pnr.controller;

import com.trainbooking.ticket.dto.TicketResponse;
import com.trainbooking.ticket.service.TicketService;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/pnr")
public class PnrController {
    private final TicketService ticketService;

    public PnrController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/{pnr}")
    public TicketResponse getTicket(
            @PathVariable @Pattern(regexp = "[A-Za-z0-9]{8,12}") String pnr,
            @RequestParam @Pattern(regexp = "[0-9]{4}") String mobileLast4) {
        return ticketService.getVerifiedTicket(pnr, mobileLast4);
    }
}
