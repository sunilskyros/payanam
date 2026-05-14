package com.sunilskyros.payanam.features.ticketcollector.validateticket;

import com.sunilskyros.payanam.data.dto.Ticket;
import com.sunilskyros.payanam.util.InputAndValidation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ValidateTicketView {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");
    private final ValidateTicketPresenter presenter;

    public ValidateTicketView() {
        this.presenter = new ValidateTicketPresenter(this);
    }

    public void init() {
        System.out.println("""
                           Validate Ticket
                           -------------------------""");
        String input = InputAndValidation.getStringInput("Enter Ticket ID to validate (or type 'exit' to go back) :");
        if (input.equalsIgnoreCase("exit")) {
            return;
        }
        presenter.validateTicket(input);
    }

    void showError(String error) {
        System.out.println("\n[ERROR] " + error);
    }

    void showSuccess(Ticket ticket) {
        System.out.println("\n[SUCCESS] Ticket is Valid!\n"+
                           "-------------------------------\n"+
                           "Ticket Id    : " + ticket.getTicketId()+"\n"+
                           "Passenger    : " + ticket.getPassengerPhoneNumber()+"\n"+
                           "Bus Number   : " + ticket.getBusId() + " (" + ticket.getBusName() + ")"+"\n"+
                           "Route        : " + ticket.getSourceStop() + " -> " + ticket.getDestinationStop());
        if (ticket.getBoughtTime() != null && ticket.getValidUntil() != null
                && ticket.getValidUntil().isAfter(LocalDateTime.now())) {
            System.out.println("Bought Time  : " + ticket.getBoughtTime().format(FORMATTER) + "\n" +
                    "Valid Until  : " + ticket.getValidUntil().format(FORMATTER));
        } else {
            System.out.println("Ticket has expired");
        }
        ticket.setIsValid(false);
    }
}
