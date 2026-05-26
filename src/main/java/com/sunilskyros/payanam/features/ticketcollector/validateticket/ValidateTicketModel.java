package com.sunilskyros.payanam.features.ticketcollector.validateticket;

import com.sunilskyros.payanam.data.dto.Ticket;
import com.sunilskyros.payanam.data.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ValidateTicketModel {

    private final TicketRepository ticketRepository;

    @Autowired
    public ValidateTicketModel(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    /**
     * Retrieves a ticket from the database using its unique ID.
     * @param ticketId The integer ID of the ticket.
     * @return The Ticket object if found, otherwise null.
     */
    public Ticket getTicketById(int ticketId) {
        return ticketRepository.findById(ticketId).orElse(null);
    }

    /**
     * Updates an existing ticket in the database.
     * Commonly used to update the 'is_valid' status after validation.
     * @param ticket The ticket object with updated fields.
     * @return true if the update was successful, false otherwise.
     */
    public Boolean updateTicket(Ticket ticket) {
        ticketRepository.save(ticket);
        return true;
    }
}
