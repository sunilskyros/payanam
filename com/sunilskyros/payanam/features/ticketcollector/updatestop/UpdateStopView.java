package com.sunilskyros.payanam.features.ticketcollector.updatestop;

import com.sunilskyros.payanam.data.dto.Bus;
import com.sunilskyros.payanam.features.ticketcollector.validateticket.ValidateTicketView;
import com.sunilskyros.payanam.util.ConsoleInput;

import java.util.Scanner;

public class UpdateStopView {
    private final UpdateStopPresenter updateStopPresenter;

    private final Scanner scanner;


    public UpdateStopView(){
        this.updateStopPresenter = new UpdateStopPresenter(this);
        this.scanner= ConsoleInput.getScanner();
    }

    public void init(Bus bus) {
        while (true) {
            System.out.println("\n\tBus Operations: " + bus.getId() + " (" + bus.getName() + ")");
            System.out.println("-------------------------------------------------------");
            System.out.println("1. Update Current Stop");
            System.out.println("2. Validate Ticket");
            System.out.println("3. Return to Main Menu");
            System.out.print("\nChoose an option : ");
            
            String choice = scanner.nextLine().trim();
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
    void updateStop(Bus bus){
        System.out.println("\nUpdate Current stop for the bus ");
        System.out.println("------------------------------------");
        updateStopPresenter.updateCurrentStop(bus);
    }
    void showMessage(String message){
        System.out.println(message);
    }
    String getInput(String message){
        System.out.println(message);
        return scanner.nextLine();
    }
}
