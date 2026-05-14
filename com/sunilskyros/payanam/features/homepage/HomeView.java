package com.sunilskyros.payanam.features.homepage;

import com.sunilskyros.payanam.data.dto.Bus;
import com.sunilskyros.payanam.data.dto.Passenger;
import com.sunilskyros.payanam.data.dto.Ticket;
import com.sunilskyros.payanam.features.ticketcollector.updatestop.UpdateStopView;
import com.sunilskyros.payanam.features.ticketcollector.validateticket.ValidateTicketView;
import com.sunilskyros.payanam.util.InputAndValidation;

import java.time.LocalDateTime;
import java.util.List;
public class HomeView {
    private final HomePresenter homePresenter;
    private final Passenger passenger;

    public HomeView(Passenger passenger) {
        this.homePresenter = new HomePresenter(this);
        this.passenger = passenger;
    }

    public void init() {
        homePresenter.init(passenger);
    }


    public void showPassengerMenu() {
        while (true) {
            showMessage(InputAndValidation.options.get("passenger_options"));
            String choice = InputAndValidation.getStringInput("Choose an option : ");
            switch (choice) {
                case "1":
                    String busInputForSearch = InputAndValidation.getStringInput("Enter Bus Number :");
                    homePresenter.searchBusByNumber(busInputForSearch);
                    break;
                case "2":
                    String stopName = InputAndValidation.getStringInput("Enter Stop Name : ");
                    homePresenter.searchBusByStop(stopName);
                    String busNum = InputAndValidation.getStringInput("Enter Bus Number : ");
                    homePresenter.searchBusByNumber(busNum);
                    break;
                case "3":
                    homePresenter.listAllBuses();
                    break;
                case "4":
                    System.out.println("""
                            Ticket Booking
                            ------------------------------""");
                    String busInputForBooking = InputAndValidation.getStringInput("Enter Bus Number : ");
                    String sourceStop = InputAndValidation.getStringInput("Enter Source Stop : ");
                    String destinationStop = InputAndValidation.getStringInput("Enter Destination Stop : ");
                    homePresenter.bookTicket(passenger, busInputForBooking, sourceStop, destinationStop);
                    break;
                case "5":
                    homePresenter.viewTickets(passenger);
                    break;
                case "6":
                    showProfile(passenger);
                    break;
                case "7":
                    System.out.println("""
                            You have been signed out.
                            Thank you for selecting us!!!""");

                    return;
                default:
                    System.out.println("\nInvalid option selected.Please try again.");
            }

        }
    }

    public void showTicketCollectorMenu() {
        while (true) {
            showMessage(InputAndValidation.options.get("ticketCollector_options"));
            String choice = InputAndValidation.getStringInput("Choose an option : ");
            switch (choice) {
                case "1":
                    homePresenter.listAllBuses();
                    break;
                case "2":
                    System.out.println("""
                                       Select Bus to operate" +
                                       --------------------------------""");
                    String busInput = InputAndValidation.getStringInput("Enter Bus Number : ");
                    Bus bus = homePresenter.selectBus(busInput);
                    if (bus!=null) {
                        new UpdateStopView().init(bus);
                    }
                    break;
                case "3":
                    new ValidateTicketView().init();
                    break;
                case "4":
                    System.out.println("""
                                       You have been signed out.
                                       Thank you for selecting us!!!""");
                    return;
                default:
                    System.out.println("\nInvalid option selected.Please try again.");
            }
        }
    }

    public void showAdminMenu() {
        while (true) {
            showMessage(InputAndValidation.options.get("admin_options"));
            String choice = InputAndValidation.getStringInput("Choose an option : ");;
            switch (choice) {
                case "1":
                    homePresenter.listAllBuses();
                    break;
                case "2":
                    System.out.println("""
                                        Add Bus
                                        --------""");
                    String busNumberForAdd = InputAndValidation.getStringInput("Enter Bus Number : ");
                    String busName = InputAndValidation.getStringInput("Enter Bus Name : ");
                    homePresenter.addBus(busNumberForAdd, busName);
                    break;
                case "3":
                    System.out.println("""
                                       Set stops for bus (This overwrites existing stops)
                                       ---------------------------------------------------""");
                    String busNumberForStops = InputAndValidation.getStringInput("Enter Bus Number : ");
                    String stopsInput = InputAndValidation.getStringInput("Enter Stops (comma separated) : ");
                    homePresenter.setStops(busNumberForStops, stopsInput);
                    break;
                case "4":
                    System.out.println("""
                                        Add Ticket Collector
                                        ----------------------""");
                    String name = InputAndValidation.getStringInput("Enter Ticket Collector Name : ");
                    String phoneNumber = InputAndValidation.getStringInput("Enter Phone Number : ");
                    String password = InputAndValidation.getPassWord("Enter Password : ");
                    password=InputAndValidation.validatePassWord(password);
                    Passenger done =homePresenter.addTicketCollector(name, phoneNumber, password);
                    if (done!=null) {
                        showMessage("Ticket colletor created successfully");
                    }
                    else {
                        showMessage("Can't create Ticket collector");
                    }
                    break;
                case "5":
                    System.out.println("""
                                        Delete Bus
                                        ------------""");
                    String busNumberForDelete = InputAndValidation.getStringInput("Enter Bus Number : ");
                    homePresenter.deleteBus(busNumberForDelete);
                    break;
                case "6":
                    System.out.println("""
                                        Delete Bus Stops
                                        -----------------""");
                    String busNumberForDeleteStops = InputAndValidation.getStringInput("Enter Bus Number : ");
                    homePresenter.deleteStops(busNumberForDeleteStops);
                    break;
                case "7":
                    System.out.println("""
                                        Delete Passenger / Ticket Collector
                                        ------------------------------------""");
                    String passengerPhoneNumber = InputAndValidation.getStringInput("Enter Phone Number : ");
                    homePresenter.removePassenger(passengerPhoneNumber);
                    break;
                case "8":
                    System.out.println("""
                                       You have been signed out.
                                       Thank you for selecting us!!!""");
                    return;
                default:
                    System.out.println("\nInvalid option selected.Please try again.");
            }
        }
    }
    void showError(String message) {
        System.out.println("\n"+message);
    }

    void showMessage(String message) {
        System.out.println("\n"+message);
    }
    void showBus(Bus bus) {
        System.out.println("Bus Number : " + bus.getId()+"\n"+
                           "Bus Name   : " + bus.getName());
    }
    void showStop(String stop) {
        System.out.println(stop);
    }
    void showBusesForStop(String stopName, List<Bus> buses) {
        System.out.println("\nBuses available for stop : " + stopName+"\n"+
                           "--------------------------------------------");
        for (Bus bus : buses) {
            showBus(bus);
        }
    }

    void showBookedTicket(Ticket ticket) {
        if (ticket!=null) {
            System.out.println("""
                               Ticket booked successfully.
                               ----------------------------""");
            System.out.println("Ticket Id : " + ticket.getTicketId()+"\n"+
                               "Bus Number : " + ticket.getBusId() + "  Bus Name : " + ticket.getBusName()+"\n"+
                               "From : " + ticket.getSourceStop() + "\nTo : " + ticket.getDestinationStop());
        }
        else {
            System.out.println("Ticket Not Found");
        }
    }

    void showTickets(List<Ticket> tickets) {
        System.out.println("""
                           Your Tickets
                           -------------""");
        for (Ticket ticket : tickets) {
            String validStr;
            if (LocalDateTime.now().isAfter(ticket.getValidUntil())) {
                validStr="Expired";
            }
            else {
                validStr="Valid";
            }
            System.out.println("Ticket Id    : " + ticket.getTicketId()+"\n"+
                               "Bus Number   : " + ticket.getBusId()+"\n"+
                               "Bus Name     : " + ticket.getBusName()+"\n"+
                               "From         : " + ticket.getSourceStop()+"\n"+
                               "To           : " + ticket.getDestinationStop()+"\n"+
                               "Ticket price : " + ticket.getPrice() + " Rs "+"\n"+
                               "Status       : " + validStr + "\n" +
                               "Valid Until  : " + (validStr.equals("Valid")?ticket.getValidUntil():"-") + "\n");
        }
    }
    void showProfile(Passenger passenger) {
        System.out.println("\n\tProfile"+
                            "------------------------\n"+
                            "Name         : " + passenger.getName()+"\n"+
                            "Phone Number : " + passenger.getPhoneNumber()+"\n"+
                            "Status       : " + passenger.getStatus()+"\n");
    }
    public void showUnauthorized() {
        System.out.println("Your account role is not set. Contact your administrator.");
    }

}
