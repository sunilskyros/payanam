package com.sunilskyros.payanam.features.realtime.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunilskyros.payanam.data.dto.LiveLocationUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class RedisMessageSubscriber implements MessageListener {

    private final RealTimeLocationService realTimeLocationService;
    private final ObjectMapper objectMapper;

    @Autowired
    public RedisMessageSubscriber(RealTimeLocationService realTimeLocationService, ObjectMapper objectMapper) {
        this.realTimeLocationService = realTimeLocationService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // Deserialize JSON payload directly from Redis channel byte stream
            LiveLocationUpdate update = objectMapper.readValue(message.getBody(), LiveLocationUpdate.class);
            if (update != null) {
                // Broadcast to all active WebSocket clients on this local application server node
                realTimeLocationService.broadcastToWebSocketSubscribers(update);
            }
        } catch (Exception e) {
            // Silent error suppression for clean logging during local testing without active Redis servers
        }
    }
}
