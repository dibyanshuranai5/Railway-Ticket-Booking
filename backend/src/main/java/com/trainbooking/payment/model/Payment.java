package com.trainbooking.payment.model;

import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "payments")
public class Payment {
    @Id
    public String id;

    @Indexed
    public String bookingId;

    public String userId;
    public BigDecimal amountINR;
    public String status;
    public String pnr;
    public Instant createdAt;
}
