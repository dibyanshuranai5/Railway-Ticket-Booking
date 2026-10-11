package com.trainbooking.booking.model;

public class Passenger {
    public String name;
    public int age;
    public String gender;
    public String berthPreference;
    public String seatNumber;

    public Passenger() {
    }

    public Passenger(String name, int age, String gender, String berthPreference, String seatNumber) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.berthPreference = berthPreference;
        this.seatNumber = seatNumber;
    }
}
