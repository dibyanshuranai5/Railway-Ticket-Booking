package com.trainbooking.booking.repository;

import com.trainbooking.booking.model.Booking;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookingRepository extends MongoRepository<Booking, String> {
    List<Booking> findByUserIdOrderByCreatedAtDesc(String userId);
    Optional<Booking> findByIdAndUserId(String id, String userId);
    Optional<Booking> findByPnr(String pnr);
}
