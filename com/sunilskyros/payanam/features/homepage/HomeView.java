package com.sunilskyros.payanam.features.homepage;

import com.sunilskyros.payanam.data.dto.Passenger;
import com.sunilskyros.payanam.util.ConsoleInput;

import java.util.Scanner;

public class HomeView {
    private final HomeModel homeModel;
    private final Passenger passenger;
    private final Scanner scanner;

    public HomeView(Passenger passenger){
        this.homeModel=new HomeModel(this);
        this.passenger=passenger;
        this.scanner= ConsoleInput.getScanner();
    }
    public void init() {
        homeModel.init(passenger);
    }

    public void showUnauthorized() {
        System.out.println("Your account role is not set. Contact your administrator.");
    }

    public void showPassengerMenu() {
        while (true){
            System.out.println();
            System.out.println("Passenger Menu");
            System.out.println("1.Seaech Bus by Number");
            System.out.println("2.Search bus by stop");
            System.out.println("3.List all buses");
            System.out.println("4.Book ticket");
            System.out.println("5.View Tickets");
            System.out.println("6.Sign Out");
            System.out.print("Choose an option : ");
            String choice = scanner.nextLine().trim();
            switch (choice){
                case "1":
                    int busNumber=scanner.nextInt();

                    break;
                case "2":
                        break;
                case "3":
                        break;
                case "4":
                        break;
                case "5":
                        break;
                case "6":
                    System.out.println("You have been signed out.");
                    System.out.println("Thank you for selecting us!!!");
                    return;
                default:
                    System.out.println("Invalid option selected.Please try again.");
            }

        }
    }

    public void showTicketCollectorMenu() {
    }
}
