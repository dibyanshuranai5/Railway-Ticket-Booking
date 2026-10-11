package com.trainbooking.payment.repository;

import com.trainbooking.payment.model.Payment;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentRepository extends MongoRepository<Payment, String> {
    Optional<Payment> findFirstByBookingIdAndStatus(String bookingId, String status);
}
