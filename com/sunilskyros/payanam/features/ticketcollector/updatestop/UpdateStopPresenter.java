package com.sunilskyros.payanam.features.ticketcollector.updatestop;

import com.sunilskyros.payanam.data.dto.Bus;
import com.sunilskyros.payanam.data.dto.Stop;

import java.time.LocalTime;
import java.util.List;

public class UpdateStopPresenter {
    private final UpdateStopView updateStopView;
    private final UpdateStopModel updateStopModel;

    public UpdateStopPresenter(UpdateStopView updateStopView) {
        this.updateStopView = updateStopView;
        this.updateStopModel = new UpdateStopModel();
    }

    /**
     * Manages the flow of updating a bus's current location.
     * Determines whether the bus is starting a journey, ending a journey,
     * or moving to the next stop, and prompts the user accordingly.
     * @param bus The bus object whose location is being tracked.
     */
    void updateCurrentStop(Bus bus) {
        List<Stop> stops = bus.getStops();
        if (stops == null || stops.isEmpty()) {
            updateStopView.showMessage("No stops available for this bus.");
            return;
        }

        int currentIndex = findCurrentStopIndex(stops);

        if (currentIndex == -1) {
            updateStopView.showMessage("Bus has already completed its route!");
            return;
        }

        Stop current = stops.get(currentIndex);

        if (Boolean.TRUE.equals(current.getCurrentStop())) {
            handleBusAtStop(bus, stops, currentIndex);
        } else {
            handleJourneyStart(stops, currentIndex);
        }
    }

    /**
     * Finds the index of the stop where the bus is currently located,
     * or the index of the next unvisited stop if the journey hasn't started.
     * @param stops The list of stops for the route.
     * @return The index of the current or next stop, or -1 if the route is complete.
     */
    private int findCurrentStopIndex(List<Stop> stops) {
        for (int i = 0; i < stops.size(); i++) {
            if (Boolean.TRUE.equals(stops.get(i).getCurrentStop()) || stops.get(i).getCurrentStop() == null) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Handles the logic for when a bus is currently at a valid stop.
     * Checks if it's the final stop, otherwise prompts to move to the next stop.
     * @param bus The bus object.
     * @param stops The list of stops.
     * @param currentIndex The index of the current stop.
     */
    private void handleBusAtStop(Bus bus, List<Stop> stops, int currentIndex) {
        Stop current = stops.get(currentIndex);

        if (currentIndex == stops.size() - 1) {
            handleFinalStop(bus, stops, current);
            return;
        }

        Stop next = stops.get(currentIndex + 1);
        updateStopView.showMessage("Bus is currently at: " + current.getStopName());
        String choice = updateStopView.getInput("Update location to next stop (" + next.getStopName() + ")? [Y/N]");

        if (choice.trim().equalsIgnoreCase("Y")) {
            current.setCurrentStop(false);
            current.setUpdatedTime(LocalTime.now());
            next.setCurrentStop(true);
            updateStopModel.calculateEstimatedTimes(stops, currentIndex);
            updateStopModel.updateStops(stops);
            updateStopView.showMessage("Stop updated to " + next.getStopName());
        }
    }

    /**
     * Handles the logic when the bus has reached its final destination.
     * Prompts the user to end the journey and optionally reverse the route.
     * @param bus The bus object.
     * @param stops The list of stops.
     * @param current The final stop object.
     */
    private void handleFinalStop(Bus bus, List<Stop> stops, Stop current) {
        updateStopView.showMessage("Bus is at the final destination: " + current.getStopName());
        String choice = updateStopView.getInput("End journey? [Y/N]");

        if (choice.trim().equalsIgnoreCase("Y")) {
            current.setCurrentStop(false);
            current.setUpdatedTime(LocalTime.now());
            updateStopModel.updateStops(stops);
            updateStopView.showMessage("Journey ended.");

            String reverseChoice = updateStopView.getInput("Change direction to go back to starting point? [Y/N]");
            if (reverseChoice.trim().equalsIgnoreCase("Y")) {
                List<Stop> reversedStops = updateStopModel.reverseRoute(stops);
                bus.setStops(reversedStops);
                updateStopModel.updateBusStops(bus);
                updateStopView.showMessage("Route reversed successfully. Starting point is now: " + reversedStops.get(0).getStopName());
            }
        }
    }

    /**
     * Handles the logic for initiating a new journey from the first stop.
     * Updates times and sets the first stop as active.
     * @param stops The list of stops.
     * @param currentIndex The index of the first stop (0).
     */
    private void handleJourneyStart(List<Stop> stops, int currentIndex) {
        Stop current = stops.get(currentIndex);
        updateStopView.showMessage("Bus has not started its journey.");
        String choice = updateStopView.getInput("Start journey at first stop (" + current.getStopName() + ")? [Y/N]");

        if (choice.trim().equalsIgnoreCase("Y")) {
            current.setCurrentStop(true);
            current.setUpdatedTime(LocalTime.now());
            updateStopModel.calculateEstimatedTimes(stops, currentIndex);
            updateStopModel.updateStops(stops);
            updateStopView.showMessage("Journey started at " + current.getStopName());
        }
    }
}
