package com.sunilskyros.payanam.features.ticketcollector.validateticket;

import com.sunilskyros.payanam.data.dto.Ticket;

import java.time.LocalDateTime;

public class ValidateTicketPresenter {
    private final ValidateTicketView validateTicketView;
    private final ValidateTicketModel validateTicketModel;

    public ValidateTicketPresenter(ValidateTicketView validateTicketView) {
        this.validateTicketView = validateTicketView;
        this.validateTicketModel = new ValidateTicketModel();
    }

    /**
     * Validates a ticket presented by a passenger.
     * Checks if the ticket exists, hasn't been used yet, and hasn't expired.
     * Updates the database to mark it as used if valid, and informs the view.
     * @param ticketIdInput The string input containing the Ticket ID.
     */
    void validateTicket(String ticketIdInput) {
        if (ticketIdInput == null || ticketIdInput.trim().isEmpty()) {
            validateTicketView.showError("Ticket ID cannot be empty.");
            return;
        }

        int ticketId;
        try {
            ticketId = Integer.parseInt(ticketIdInput.trim());
        } catch (NumberFormatException e) {
            validateTicketView.showError("Invalid Ticket ID. Please enter numbers only.");
            return;
        }

        Ticket ticket = validateTicketModel.getTicketById(ticketId);
        if (ticket == null) {
            validateTicketView.showError("Invalid Ticket: No ticket found with ID " + ticketId);
        } else if (!ticket.getIsValid()) {
            validateTicketView.showError("Invalid Ticket: This ticket is already used");
        } else if (ticket.getValidUntil() != null && LocalDateTime.now().isAfter(ticket.getValidUntil())) {
            validateTicketView.showError("Expired Ticket: This ticket expired at " + ticket.getValidUntil());
        } else {
            ticket.setIsValid(false);
            Boolean valid = validateTicketModel.updateTicket(ticket);
            if (valid) {
                validateTicketView.showSuccess(ticket);
            } else {
                validateTicketView.showError("Error: Could not validate ticket");
            }
        }
    }
}
