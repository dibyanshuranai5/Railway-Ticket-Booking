package com.trainbooking.booking.dto;

public record PassengerResponse(
        String name,
        int age,
        String gender,
        String berthPreference,
        String seatNumber) {
}
