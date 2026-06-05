package com.sunilskyros.payanam.features.ticketcollector.updatestop;

import com.sunilskyros.payanam.data.dto.Bus;
import com.sunilskyros.payanam.data.dto.Stop;
import com.sunilskyros.payanam.data.repository.StopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UpdateStopModel {

    private final StopRepository stopRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final long ESTIMATED_MINUTES_PER_STOP = 15L;

    @Autowired
    public UpdateStopModel(StopRepository stopRepository, RedisTemplate<String, Object> redisTemplate) {
        this.stopRepository = stopRepository;
        this.redisTemplate = redisTemplate;
    }

    /**
     * Persists updates to a list of stops (times and current stop status) in the database and invalidates cache.
     * @param stops The list of modified stops.
     */
    @Transactional
    public void updateStops(List<Stop> stops) {
        stopRepository.saveAll(stops);
        if (stops != null && !stops.isEmpty()) {
            try {
                redisTemplate.delete("payanam:bus:" + stops.get(0).getBusId());
            } catch (Exception ignored) {}
        }
    }

    /**
     * Fully replaces and persists the list of stops for a specific bus and invalidates cache.
     * Used when reversing routes.
     * @param bus The bus object containing the new route.
     */
    @Transactional
    public void updateBusStops(Bus bus) {
        if (bus == null) return;
        stopRepository.deleteByBusId(bus.getId());
        if (bus.getStops() != null && !bus.getStops().isEmpty()) {
            for (Stop stop : bus.getStops()) {
                stop.setBusId(bus.getId());
            }
            stopRepository.saveAll(bus.getStops());
        }
        try {
            redisTemplate.delete("payanam:bus:" + bus.getId());
        } catch (Exception ignored) {}
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
        LocalTime now = LocalTime.now();
        for (int i = stops.size() - 1; i >= 0; i--) {
            Stop oldStop = stops.get(i);
            Stop newStop = new Stop();
            newStop.setId(stops.size() - i);
            newStop.setBusId(oldStop.getBusId());
            newStop.setStopName(oldStop.getStopName());
            if (i == stops.size() - 1) {
                newStop.setUpdatedTime(now);
                newStop.setCurrentStop(true);
            } else {
                newStop.setUpdatedTime(LocalTime.of(0, 0));
                newStop.setCurrentStop(false);
            }
            reversedStops.add(newStop);
        }
        calculateEstimatedTimes(reversedStops, 0);
        return reversedStops;
    }
}