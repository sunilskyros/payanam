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
        } else if (ticket.getValidUntil() != null && LocalDateTime.now().isAfter(ticket.getValidUntil())) {
            validateTicketView.showError("Expired Ticket: This ticket expired at " + ticket.getValidUntil());
        } else {
            validateTicketView.showSuccess(ticket);
        }
    }
}
