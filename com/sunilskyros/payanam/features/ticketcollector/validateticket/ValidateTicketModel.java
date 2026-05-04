package com.sunilskyros.payanam.features.ticketcollector.validateticket;

import com.sunilskyros.payanam.data.dto.Ticket;
import com.sunilskyros.payanam.data.repository.PayanamDB;

public class ValidateTicketModel {

    public Ticket getTicketById(int ticketId) {
        return PayanamDB.getInstance().getTicketById(ticketId);
    }
    
}
