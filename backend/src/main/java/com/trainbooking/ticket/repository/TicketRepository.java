package com.trainbooking.ticket.repository;

import com.trainbooking.ticket.model.Ticket;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TicketRepository extends MongoRepository<Ticket, String> {
    Optional<Ticket> findByPnr(String pnr);
    boolean existsByPnr(String pnr);
}
