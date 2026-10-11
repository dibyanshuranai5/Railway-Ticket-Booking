package com.trainbooking.booking.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "bookings")
public class Booking {
    @Id
    public String id;

    @Indexed
    public String userId;

    public String contactPhone;
    public String trainNumber;
    public String trainName;
    public String fromStation;
    public String toStation;
    public LocalDate journeyDate;
    public String departure;
    public String arrival;
    public String classCode;
    public List<Passenger> passengers = new ArrayList<>();
    public BigDecimal fareINR;
    public String status;
    public String pnr;
    public Instant createdAt;
}
