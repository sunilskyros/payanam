package com.sunilskyros.payanam.features.ticketcollector.updatestop;

import com.sunilskyros.payanam.data.dto.Bus;
import com.sunilskyros.payanam.data.dto.Stop;
import com.sunilskyros.payanam.data.repository.PayanamDB;

import java.time.LocalTime;
import java.util.List;

public class UpdateStopPresenter {
    private final UpdateStopView updateStopView;

    public UpdateStopPresenter(UpdateStopView updateStopView) {
        this.updateStopView = updateStopView;
    }

    void updateCurrentStop(Bus bus) {
        List<Stop> stops = bus.getStops();
        if (stops == null || stops.isEmpty()) {
            updateStopView.showMessage("No stops available for this bus.");
            return;
        }

        int currentIndex = -1;
        for (int i = 0; i < stops.size(); i++) {
            if (Boolean.TRUE.equals(stops.get(i).getCurrentStop())) {
                currentIndex = i;
                break;
            } else if (stops.get(i).getCurrentStop() == null) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex == -1) {
            updateStopView.showMessage("Bus has already completed its route!");
            return;
        }

        Stop current = stops.get(currentIndex);

        if (Boolean.TRUE.equals(current.getCurrentStop())) {
            if (currentIndex == stops.size() - 1) {
                updateStopView.showMessage("Bus is at the final destination: " + current.getStopName());
                String choice = updateStopView.getInput("End journey? [Y/N]");
                if (choice.trim().equalsIgnoreCase("Y")) {
                    current.setCurrentStop(false);
                    current.setUpdatedTime(LocalTime.now());
                    PayanamDB.getInstance().updateStops(stops);
                    updateStopView.showMessage("Journey ended.");
                    
                    String reverseChoice = updateStopView.getInput("Change direction to go back to starting point? [Y/N]");
                    if (reverseChoice.trim().equalsIgnoreCase("Y")) {
                        java.util.List<Stop> reversedStops = new java.util.ArrayList<>();
                        for (int i = stops.size() - 1; i >= 0; i--) {
                            Stop oldStop = stops.get(i);
                            Stop newStop = new Stop();
                            newStop.setId(stops.size() - i);
                            newStop.setBusId(oldStop.getBusId());
                            newStop.setStopName(oldStop.getStopName());
                            newStop.setUpdatedTime(LocalTime.of(0, 0));
                            newStop.setCurrentStop(null);
                            reversedStops.add(newStop);
                        }
                        bus.setStop(reversedStops);
                        PayanamDB.getInstance().updateBusStops(bus);
                        updateStopView.showMessage("Route reversed successfully. Starting point is now: " + reversedStops.get(0).getStopName());
                    }
                }
                return;
            }

            Stop next = stops.get(currentIndex + 1);
            updateStopView.showMessage("Bus is currently at: " + current.getStopName());
            String choice = updateStopView.getInput("Update location to next stop (" + next.getStopName() + ")? [Y/N]");
            if (choice.trim().equalsIgnoreCase("Y")) {
                current.setCurrentStop(false);
                current.setUpdatedTime(LocalTime.now());
                next.setCurrentStop(true);
                for (int j = currentIndex+1; j < stops.size(); j++) {
                    stops.get(j).setUpdatedTime(stops.get(j-1).getUpdatedTime().plusMinutes(15L));
                }
                PayanamDB.getInstance().updateStops(stops);
                updateStopView.showMessage("Stop updated to " + next.getStopName());
            }
        } else {
            updateStopView.showMessage("Bus has not started its journey.");
            String choice = updateStopView.getInput("Start journey at first stop (" + current.getStopName() + ")? [Y/N]");
            if (choice.trim().equalsIgnoreCase("Y")) {
                current.setCurrentStop(true);
                current.setUpdatedTime(LocalTime.now());
                for (int j = currentIndex+1; j < stops.size(); j++) {
                    stops.get(j).setUpdatedTime(stops.get(j-1).getUpdatedTime().plusMinutes(15L));
                }
                PayanamDB.getInstance().updateStops(stops);
                updateStopView.showMessage("Journey started at " + current.getStopName());
            }
        }
    }
}
