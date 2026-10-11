package com.trainbooking.pnr.model;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "pnrs")
public class PnrRecord {
    @Id
    public String pnr;

    public String ticketId;
    public String bookingId;
    public Instant createdAt;

    public PnrRecord() {
    }

    public PnrRecord(String pnr, String ticketId, String bookingId) {
        this.pnr = pnr;
        this.ticketId = ticketId;
        this.bookingId = bookingId;
        this.createdAt = Instant.now();
    }
}
