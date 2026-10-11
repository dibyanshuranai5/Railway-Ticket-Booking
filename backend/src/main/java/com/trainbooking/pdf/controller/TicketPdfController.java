package com.trainbooking.pdf.controller;

import com.trainbooking.pdf.service.TicketPdfService;
import com.trainbooking.ticket.service.TicketService;
import jakarta.validation.constraints.Pattern;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/tickets")
public class TicketPdfController {
    private final TicketService ticketService;
    private final TicketPdfService pdfService;

    public TicketPdfController(TicketService ticketService, TicketPdfService pdfService) {
        this.ticketService = ticketService;
        this.pdfService = pdfService;
    }

    @GetMapping(value = "/{pnr}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<ByteArrayResource> download(
            @PathVariable @Pattern(regexp = "[A-Za-z0-9]{8,12}") String pnr,
            @RequestParam @Pattern(regexp = "[0-9]{4}") String mobileLast4) {
        var ticket = ticketService.getVerifiedTicket(pnr, mobileLast4);
        byte[] pdf = pdfService.create(ticket);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"railway-ticket-" + ticket.pnr() + ".pdf\"")
                .contentLength(pdf.length)
                .body(new ByteArrayResource(pdf));
    }
}
