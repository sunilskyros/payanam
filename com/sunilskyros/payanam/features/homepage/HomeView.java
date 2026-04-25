package com.sunilskyros.payanam.features.homepage;

import com.sunilskyros.payanam.data.dto.Bus;
import com.sunilskyros.payanam.data.dto.Passenger;
import com.sunilskyros.payanam.data.dto.Stop;
import com.sunilskyros.payanam.data.dto.Ticket;
import com.sunilskyros.payanam.data.repository.PayanamDB;
import com.sunilskyros.payanam.util.ConsoleInput;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class HomeView {
    private final HomeModel homeModel;
    private final Passenger passenger;
    private final Scanner scanner;

    public HomeView(Passenger passenger) {
        this.homeModel = new HomeModel(this);
        this.passenger = passenger;
        this.scanner = ConsoleInput.getScanner();
    }

    public void init() {
        homeModel.init(passenger);
    }

    public void showUnauthorized() {
        System.out.println("Your account role is not set. Contact your administrator.");
    }

    public void showPassengerMenu() {
        while (true) {
            System.out.println();
            System.out.println("Passenger Menu");
            System.out.println("1.Search Bus by Number");
            System.out.println("2.Search Bus by Stop");
            System.out.println("3.List all buses");
            System.out.println("4.Book ticket");
            System.out.println("5.View Tickets");
            System.out.println("6.Profile details");
            System.out.println("7.Sign Out");
            System.out.print("Choose an option : ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    System.out.print("Enter Bus Number : ");
                    String busInputForSearch = scanner.nextLine().trim();
                    homeModel.searchBusByNumber(busInputForSearch);
                    break;
                case "2":
                    System.out.print("Enter Stop Name : ");
                    String stopName = scanner.nextLine().trim();
                    homeModel.searchBusByStop(stopName);
                    break;
                case "3":
                    homeModel.listAllBuses();
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
                    homeModel.bookTicket(passenger, busInputForBooking, sourceStop, destinationStop);
                    break;
                case "5":
                    homeModel.viewTickets(passenger);
                    break;
                case "6":
                    System.out.println("\n\tProfile");
                    System.out.println("------------------------");
                    System.out.println("Name : " + passenger.getName());
                    System.out.println("Phone Number : " + passenger.getPhoneNumber());
                    System.out.println("Status : " + passenger.getStatus());
                    break;
                case "7":
                    System.out.println("You have been signed out.");
                    System.out.println("Thank you for selecting us!!!");
                    return;
                default:
                    System.out.println("Invalid option selected.Please try again.");
            }

        }
    }

    public void showTicketCollectorMenu() {
        while (true) {
            System.out.println();
            System.out.println("Ticket Collector Menu");
            System.out.println("-----------------------");
            System.out.println("1.Bus List");
            System.out.println("2.Select Bus");
            System.out.println("3.Add Bus");
            System.out.println("4.Add Stops");
            System.out.println("5.Sign Out");
            System.out.print("Choose an option : ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    homeModel.listAllBuses();
                    break;
                case "2":
                    System.out.println("Enter Bus Number :");
                    String busInput = scanner.nextLine().trim();
                    homeModel.selectBus(busInput);
                    break;
                case "3":
                    System.out.print("\nEnter Bus Number : ");
                    String busNumberForAdd = scanner.nextLine().trim();
                    System.out.print("Enter Bus Name : ");
                    String busName = scanner.nextLine().trim();
                    homeModel.addBus(busNumberForAdd, busName);
//                    break;
                case "4":
                    System.out.println("Add stops to bus");
                    System.out.println("------------------");
                    System.out.print("\nEnter Bus Number : ");
                    String busNumberForStops = scanner.nextLine().trim();
                    System.out.print("Enter Stops (comma separated) : ");
                    String stopsInput = scanner.nextLine().trim();
                    homeModel.addStops(busNumberForStops, stopsInput);
                    break;
                case "5":
                    System.out.println("You have been signed out.");
                    System.out.println("Thank you for selecting us!!!");
                    return;
                default:
                    System.out.println("Invalid option selected.Please try again.");
            }
        }
    }
    void showError(String message){
        System.out.println(message);
    }

    void showMessage(String message) {
        System.out.println(message);
    }

    void showBus(Bus bus) {
        System.out.println("Bus Number : " + bus.getId());
        System.out.println("Bus Name : " + bus.getName());
    }
    void showStop(String stop){
        System.out.println(stop);
    }
    void showBusesForStop(String stopName, List<Bus> buses) {
        System.out.println("Buses available for stop: " + stopName);
        System.out.println("-------------------------------------");
        for (Bus bus : buses) {
            showBus(bus);
        }
    }

    void showBookedTicket(Ticket ticket) {
        System.out.println("Ticket booked successfully.");
        System.out.println("----------------------------");
        System.out.println("Ticket Id : " + ticket.getTicketId());
        System.out.println("Bus Number : " + ticket.getBusId() + " Bus Name : " + ticket.getBusName());
        System.out.println("From : " + ticket.getSourceStop() + " To : " + ticket.getDestinationStop());
    }

    void showTickets(List<Ticket> tickets) {
        System.out.println("Your Tickets");
        System.out.println("---------------");
        for (Ticket ticket : tickets) {
            System.out.println("Ticket Id : " + ticket.getTicketId());
            System.out.println("Bus Number : " + ticket.getBusId() + " Bus Name : " + ticket.getBusName());
            System.out.println("From : " + ticket.getSourceStop() + " To : " + ticket.getDestinationStop());
            System.out.println("Ticket price : "+ticket.getPrice()+" Rs ");
        }
    }
}
