package com.sunilskyros.payanam.data.repository;

import com.sunilskyros.payanam.data.dto.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    List<Ticket> findByPassengerPhoneNumberOrderByTicketIdDesc(String phoneNumber);
    List<Ticket> findAllByOrderByTicketIdDesc();
    java.util.Optional<Ticket> findByBookingReference(String bookingReference);
}
