package com.sunilskyros.payanam.features.homepage;

import com.sunilskyros.payanam.data.dto.Bus;
import com.sunilskyros.payanam.data.dto.Passenger;
import com.sunilskyros.payanam.data.dto.Ticket;
import com.sunilskyros.payanam.features.ticketcollector.updatestop.UpdateStopView;
import com.sunilskyros.payanam.features.ticketcollector.validateticket.ValidateTicketView;
import com.sunilskyros.payanam.util.ConsoleInput;

import java.util.List;
import java.util.Scanner;

public class HomeView {
    private final HomePresenter homePresenter;
    private final Passenger passenger;
    private final Scanner scanner;

    public HomeView(Passenger passenger) {
        this.homePresenter = new HomePresenter(this);
        this.passenger = passenger;
        this.scanner = ConsoleInput.getScanner();
    }

    public void init() {
        homePresenter.init(passenger);
    }

    public void showUnauthorized() {
        System.out.println("Your account role is not set. Contact your administrator.");
    }

    public void showPassengerMenu() {
        while (true) {
            System.out.println();
            System.out.println("\n\tPassenger Menu");
            System.out.println("------------------------");
            System.out.println("1.Search Bus by Number");
            System.out.println("2.Search Bus by Stop");
            System.out.println("3.List all buses");
            System.out.println("4.Book ticket");
            System.out.println("5.View Tickets");
            System.out.println("6.Profile details");
            System.out.println("7.Sign Out");
            System.out.print("\nChoose an option : ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    System.out.print("\nEnter Bus Number : ");
                    String busInputForSearch = scanner.nextLine().trim();
                    homePresenter.searchBusByNumber(busInputForSearch);
                    break;
                case "2":
                    System.out.print("\nEnter Stop Name : ");
                    String stopName = scanner.nextLine().trim();
                    homePresenter.searchBusByStop(stopName);
                    System.out.print("Enter Bus Number : ");
                    String busNum = scanner.nextLine().trim();
                    homePresenter.searchBusByNumber(busNum);
                    break;
                case "3":
                    homePresenter.listAllBuses();
                    break;
                case "4":
                    System.out.println("\n\tTicket Booking ");
                    System.out.println("------------------------------");
                    System.out.print("Enter Bus Number : ");
                    String busInputForBooking = scanner.nextLine().trim();
                    System.out.print("Enter Source Stop : ");
                    String sourceStop = scanner.nextLine().trim();
                    System.out.print("Enter Destination Stop : ");
                    String destinationStop = scanner.nextLine().trim();
                    homePresenter.bookTicket(passenger, busInputForBooking, sourceStop, destinationStop);
                    break;
                case "5":
                    homePresenter.viewTickets(passenger);
                    break;
                case "6":
                    System.out.println("\n\tProfile");
                    System.out.println("------------------------");
                    System.out.println("Name         : " + passenger.getName());
                    System.out.println("Phone Number : " + passenger.getPhoneNumber());
                    System.out.println("Status       : " + passenger.getStatus());
                    break;
                case "7":
                    System.out.println("\nYou have been signed out.");
                    System.out.println("\nThank you for selecting us!!!");
                    return;
                default:
                    System.out.println("\nInvalid option selected.Please try again.");
            }

        }
    }

    public void showTicketCollectorMenu() {
        while (true) {
            System.out.println();
            System.out.println("\n\tTicket Collector Menu");
            System.out.println("-----------------------------");
            System.out.println("1.Bus List");
            System.out.println("2.Select Bus");
            System.out.println("3.Validate Ticket");
            System.out.println("4.Sign Out");
            System.out.print("\nChoose an option : ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    homePresenter.listAllBuses();
                    break;
                case "2":
                    System.out.println("\n\tSelect Bus to operate ");
                    System.out.println("--------------------------------");
                    System.out.println("\nEnter Bus Number :");
                    String busInput = scanner.nextLine().trim();
                    Bus bus = homePresenter.selectBus(busInput);
                    if(bus!=null){
                        new UpdateStopView().init(bus);
                    }
                    break;
                case "3":
                    new ValidateTicketView().init();
                    break;
                case "4":
                    System.out.println("\nYou have been signed out.");
                    System.out.println("\nThank you for selecting us!!!");
                    return;
                default:
                    System.out.println("\nInvalid option selected.Please try again.");
            }
        }
    }

    public void showAdminMenu() {
        while (true) {
            System.out.println();
            System.out.println("\n\tAdmin Menu");
            System.out.println("-----------------------------");
            System.out.println("1.Bus List");
            System.out.println("2.Add Bus");
            System.out.println("3.Add Stops");
            System.out.println("4.Delete Bus");
            System.out.println("5.Delete Stops");
            System.out.println("6.Sign Out");
            System.out.print("\nChoose an option : ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    homePresenter.listAllBuses();
                    break;
                case "2":
                    System.out.println("\n\tAdd Bus ");
                    System.out.println("-------------------");
                    System.out.print("\nEnter Bus Number : ");
                    String busNumberForAdd = scanner.nextLine().trim();
                    System.out.print("Enter Bus Name : ");
                    String busName = scanner.nextLine().trim();
                    homePresenter.addBus(busNumberForAdd, busName);
                    break;
                case "3":
                    System.out.println("\n\tSet stops for bus (This overwrites existing stops)");
                    System.out.println("------------------");
                    System.out.print("\nEnter Bus Number : ");
                    String busNumberForStops = scanner.nextLine().trim();
                    System.out.print("Enter Stops (comma separated) : ");
                    String stopsInput = scanner.nextLine().trim();
                    homePresenter.setStops(busNumberForStops, stopsInput);
                    break;
                case "4":
                    System.out.println("\n\tDelete Bus ");
                    System.out.println("-------------------");
                    System.out.print("\nEnter Bus Number : ");
                    String busNumberForDelete = scanner.nextLine().trim();
                    homePresenter.deleteBus(busNumberForDelete);
                    break;
                case "5":
                    System.out.println("\n\tDelete Stops ");
                    System.out.println("-------------------");
                    System.out.print("\nEnter Bus Number : ");
                    String busNumberForDeleteStops = scanner.nextLine().trim();
                    homePresenter.deleteStops(busNumberForDeleteStops);
                    break;
                case "6":
                    System.out.println("\nYou have been signed out.");
                    System.out.println("\nThank you for selecting us!!!");
                    return;
                default:
                    System.out.println("\nInvalid option selected.Please try again.");
            }
        }
    }
    void showError(String message){
        System.out.println("\n"+message);
    }

    void showMessage(String message) {
        System.out.println("\n"+message);
    }

    void showBus(Bus bus) {
        System.out.println();
        System.out.println("Bus Number : " + bus.getId());
        System.out.println("Bus Name   : " + bus.getName());
    }
    void showStop(String stop){
        System.out.println(stop);
    }
    void showBusesForStop(String stopName, List<Bus> buses) {
        System.out.println("\nBuses available for stop: " + stopName);
        System.out.println("-------------------------------------");
        for (Bus bus : buses) {
            showBus(bus);
        }
    }

    void showBookedTicket(Ticket ticket) {
        if(ticket!=null) {
            System.out.println("Ticket booked successfully.");
            System.out.println("----------------------------");
            System.out.println("Ticket Id : " + ticket.getTicketId());
            System.out.println("Bus Number : " + ticket.getBusId() + " Bus Name : " + ticket.getBusName());
            System.out.println("From : " + ticket.getSourceStop() + " To : " + ticket.getDestinationStop());
        }
        else{
            System.out.println("Ticket Not Found");
        }
    }

    void showTickets(List<Ticket> tickets) {
        System.out.println("\n\tYour Tickets");
        System.out.println("---------------");
        for (Ticket ticket : tickets) {
            System.out.println("Ticket Id    : " + ticket.getTicketId());
            System.out.println("Bus Number   : " + ticket.getBusId());
            System.out.println("Bus Name     : " + ticket.getBusName());
            System.out.println("From         : " + ticket.getSourceStop());
            System.out.println("To           : " + ticket.getDestinationStop());
            System.out.println("Ticket price : " + ticket.getPrice()+" Rs ");
        }
    }
}