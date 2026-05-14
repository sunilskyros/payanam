package com.sunilskyros.payanam.features.ticketcollector.updatestop;

import com.sunilskyros.payanam.data.dto.Bus;
import com.sunilskyros.payanam.data.dto.Stop;
import com.sunilskyros.payanam.data.repository.PayanamDB;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class UpdateStopModel {

    private static final long ESTIMATED_MINUTES_PER_STOP = 15L;

    /**
     * Persists updates to a list of stops (times and current stop status) in the database.
     * @param stops The list of modified stops.
     */
    public void updateStops(List<Stop> stops) {
        PayanamDB.getInstance().updateStops(stops);
    }

    /**
     * Fully replaces and persists the list of stops for a specific bus.
     * Used when reversing routes.
     * @param bus The bus object containing the new route.
     */
    public void updateBusStops(Bus bus) {
        PayanamDB.getInstance().updateBusStops(bus);
    }

    /**
     * Calculates estimated arrival times for all subsequent stops in a route.
     * Uses a fixed interval added to the previous stop's time.
     * @param stops The full list of stops.
     * @param fromIndex The index of the current active stop.
     */
    public void calculateEstimatedTimes(List<Stop> stops, int fromIndex) {
        for (int i = fromIndex + 1; i < stops.size(); i++) {
            stops.get(i).setUpdatedTime(
                    stops.get(i - 1).getUpdatedTime().plusMinutes(ESTIMATED_MINUTES_PER_STOP)
            );
        }
    }

    /**
     * Generates a new reversed route for a bus.
     * Used when a bus reaches its final destination and needs to travel back.
     * Clears all timestamps and statuses for the new journey.
     * @param stops The original list of stops.
     * @return A new, reversed list of Stop objects.
     */
    public List<Stop> reverseRoute(List<Stop> stops) {
        List<Stop> reversedStops = new ArrayList<>();
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
        return reversedStops;
    }
}