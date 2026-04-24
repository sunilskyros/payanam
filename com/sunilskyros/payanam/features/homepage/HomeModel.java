package com.sunilskyros.payanam.features.homepage;

import com.sunilskyros.payanam.data.dto.Passenger;

public class HomeModel {
    private final HomeView homeView;

    HomeModel(HomeView homeView){
        this.homeView=homeView;
    }
    void init(Passenger passenger){
        if (passenger == null || passenger.getRole() == null) {
            homeView.showUnauthorized();
            return;
        }
        if (passenger.getRole() == Passenger.Role.PASSENGER) {
            homeView.showPassengerMenu();
        } else {
            homeView.showTicketCollectorMenu();
        }
    }
}
