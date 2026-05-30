package com.sunilskyros.payanam.features.realtime.service;

import com.sunilskyros.payanam.data.dto.Bus;
import com.sunilskyros.payanam.data.dto.BusLocationHistory;
import com.sunilskyros.payanam.data.dto.LiveLocationUpdate;
import com.sunilskyros.payanam.data.dto.Stop;
import com.sunilskyros.payanam.data.repository.BusLocationHistoryRepository;
import com.sunilskyros.payanam.data.repository.StopRepository;
import com.sunilskyros.payanam.data.repository.TicketRepository;
import com.sunilskyros.payanam.features.homepage.HomeModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@EnableAsync
public class RealTimeLocationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic locationTopic;
    
    private final BusLocationHistoryRepository locationHistoryRepository;
    private final StopRepository stopRepository;
    private final TicketRepository ticketRepository;
    private final HomeModel homeModel;

    // In-memory cache to debounce frequent GPS updates before database writes
    private final Map<Integer, LocalDateTime> lastDbWriteCache = new ConcurrentHashMap<>();
    private static final long DB_WRITE_DEBOUNCE_SECONDS = 15;

    @Autowired
    public RealTimeLocationService(SimpMessagingTemplate messagingTemplate,
                                   RedisTemplate<String, Object> redisTemplate,
                                   ChannelTopic locationTopic,
                                   BusLocationHistoryRepository locationHistoryRepository,
                                   StopRepository stopRepository,
                                   TicketRepository ticketRepository,
                                   HomeModel homeModel) {
        this.messagingTemplate = messagingTemplate;
        this.redisTemplate = redisTemplate;
        this.locationTopic = locationTopic;
        this.locationHistoryRepository = locationHistoryRepository;
        this.stopRepository = stopRepository;
        this.ticketRepository = ticketRepository;
        this.homeModel = homeModel;
    }

    /**
     * Accepts a real-time update, stores it in dynamic cache, propagates it via Redis Pub/Sub,
     * and debounces/saves to the history log in the database asynchronously.
     */
    public void processLocationUpdate(LiveLocationUpdate update, String operatorPhone) {
        // 1. Enrich stops and name from memory/DB cache if not loaded
        Bus bus = homeModel.getBusByNumber(update.getBusId());
        if (bus != null) {
            update.setBusName(bus.getName());
            if (update.getStops() == null || update.getStops().isEmpty()) {
                update.setStops(bus.getStops());
            }
        }

        // 2. Fetch live passenger count (active valid tickets for this bus route)
        long count = ticketRepository.findAll().stream()
                .filter(t -> t.getBusId() == update.getBusId() && Boolean.TRUE.equals(t.getIsValid()))
                .count();
        update.setPassengerCount((int) count);

        if (update.getUpdatedTime() == null) {
            update.setUpdatedTime(LocalTime.now().toString().substring(0, 5));
        }

        // 3. Publish to Redis Pub/Sub channel for cluster-wide node sync
        try {
            redisTemplate.convertAndSend(locationTopic.getTopic(), update);
        } catch (Exception e) {
            // Fallback: Directly broadcast via WebSocket in single-instance environments if Redis is unavailable
            broadcastToWebSocketSubscribers(update);
        }

        // 4. Asynchronously and debounced save to the persistent database history log
        saveHistoryDebounced(update, operatorPhone);
    }

    /**
     * Directly broadcasts the location update payload to connected WebSocket subscribers on the bus topic.
     */
    public void broadcastToWebSocketSubscribers(LiveLocationUpdate update) {
        String destination = "/topic/bus/" + update.getBusId();
        messagingTemplate.convertAndSend(destination, update);
    }

    @Async
    protected void saveHistoryDebounced(LiveLocationUpdate update, String operatorPhone) {
        int busId = update.getBusId();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastWrite = lastDbWriteCache.get(busId);

        // Check if last DB write is within debounce window
        if (lastWrite == null || lastWrite.plusSeconds(DB_WRITE_DEBOUNCE_SECONDS).isBefore(now)) {
            BusLocationHistory history = new BusLocationHistory();
            history.setBusId(busId);
            history.setLatitude(update.getLatitude());
            history.setLongitude(update.getLongitude());
            history.setCurrentStopId(update.getCurrentStopSeq());
            history.setCurrentStopName(update.getCurrentStopName());
            history.setTimestamp(now);
            history.setUpdatedBy(operatorPhone != null ? operatorPhone : "SYSTEM");

            locationHistoryRepository.save(history);
            lastDbWriteCache.put(busId, now);
        }
    }

    /**
     * Broadcasts telemetry notifications to administrative dashboards on topic `/topic/admin`.
     */
    public void broadcastAdminNotification(String eventType) {
        try {
            messagingTemplate.convertAndSend("/topic/admin", java.util.Map.of(
                "eventType", eventType,
                "timestamp", java.time.LocalDateTime.now().toString()
            ));
        } catch (Exception e) {
            // Suppress template broadcast noise during startup
        }
    }
}
