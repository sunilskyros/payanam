package com.sunilskyros.payanam.features.ticketcollector.validateticket;

import com.sunilskyros.payanam.data.dto.Ticket;
import com.sunilskyros.payanam.util.ConsoleInput;

import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class ValidateTicketView {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");
    private final ValidateTicketPresenter presenter;
    private final Scanner scanner;

    public ValidateTicketView() {
        this.presenter = new ValidateTicketPresenter(this);
        this.scanner = ConsoleInput.getScanner();
    }

    public void init() {
        System.out.println("\n\tValidate Ticket");
        System.out.println("-------------------------");
        System.out.print("Enter Ticket ID to validate (or type 'exit' to go back): ");
        String input = scanner.nextLine().trim();

        if (input.equalsIgnoreCase("exit")) {
            return;
        }

        presenter.validateTicket(input);
    }

    void showError(String error) {
        System.out.println("\n[ERROR] " + error);
    }

    void showSuccess(Ticket ticket) {
        System.out.println("\n[SUCCESS] Ticket is Valid!");
        System.out.println("-------------------------------");
        System.out.println("Ticket Id    : " + ticket.getTicketId());
        System.out.println("Passenger    : " + ticket.getPassengerPhoneNumber());
        System.out.println("Bus Number   : " + ticket.getBusId() + " (" + ticket.getBusName() + ")");
        System.out.println("Route        : " + ticket.getSourceStop() + " -> " + ticket.getDestinationStop());
        if (ticket.getBoughtTime() != null) {
            System.out.println("Bought Time  : " + ticket.getBoughtTime().format(FORMATTER));
            System.out.println("Valid Until  : " + ticket.getValidUntil().format(FORMATTER));
        }
    }
}
