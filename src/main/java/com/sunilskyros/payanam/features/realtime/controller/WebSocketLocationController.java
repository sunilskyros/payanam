package com.sunilskyros.payanam.features.realtime.controller;

import com.sunilskyros.payanam.data.dto.LiveLocationUpdate;
import com.sunilskyros.payanam.features.realtime.service.RealTimeLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class WebSocketLocationController {

    private final RealTimeLocationService realTimeLocationService;

    @Autowired
    public WebSocketLocationController(RealTimeLocationService realTimeLocationService) {
        this.realTimeLocationService = realTimeLocationService;
    }

    /**
     * Map client-side updates pushed to destination endpoint `/app/bus/update`.
     * Validates headers, parses security contexts, and delegates to the publisher service.
     */
    @MessageMapping("/bus/update")
    public void receiveLiveUpdate(@Payload LiveLocationUpdate update, SimpMessageHeaderAccessor headerAccessor) {
        if (update == null || update.getBusId() <= 0) {
            return;
        }

        // Get authenticated user identifier from security principal context
        Principal principal = headerAccessor.getUser();
        String operatorPhone = principal != null ? principal.getName() : "DRIVER-APP";

        // Process location publishing, scaling, and database history logs
        realTimeLocationService.processLocationUpdate(update, operatorPhone);
    }
}
