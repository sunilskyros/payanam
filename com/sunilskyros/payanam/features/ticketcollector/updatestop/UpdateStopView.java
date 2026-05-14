package com.sunilskyros.payanam.features.ticketcollector.updatestop;

import com.sunilskyros.payanam.data.dto.Bus;
import com.sunilskyros.payanam.features.ticketcollector.validateticket.ValidateTicketView;
import com.sunilskyros.payanam.util.InputAndValidation;


public class UpdateStopView {
    private final UpdateStopPresenter updateStopPresenter;

    public UpdateStopView() {
        this.updateStopPresenter = new UpdateStopPresenter(this);
    }

    public void init(Bus bus) {
        while (true) {
            System.out.println("\nBus Operations: " + bus.getId() + " (" + bus.getName() + ")");
            System.out.println("""
                                -------------------------------------------------------
                                1. Update Current Stop
                                2. Validate Ticket
                                3. Return to Main Menu
                                """);

            String choice = InputAndValidation.getStringInput("Choose option : ");
            switch (choice) {
                case "1":
                    updateStop(bus);
                    break;
                case "2":
                    new ValidateTicketView().init();
                    break;
                case "3":
                    return;
                default:
                    System.out.println("\nInvalid option selected. Please try again.");
            }
        }
    }
    void updateStop(Bus bus) {
        System.out.println("""
                            Update Current stop for the bus
                            ------------------------------------
                            """);
        updateStopPresenter.updateCurrentStop(bus);
    }
    void showMessage(String message) {
        System.out.println(message);
    }
    String getInput(String message) {
        System.out.println(message);
        return InputAndValidation.getStringInput(message);
    }
}
