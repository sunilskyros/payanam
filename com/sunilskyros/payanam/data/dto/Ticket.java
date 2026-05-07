package com.sunilskyros.payanam.data.dto;

import java.time.LocalDateTime;

public class Ticket {

    private int ticketId;
    private String passengerPhoneNumber;
    private int busId;
    private String busName;
    private String sourceStop;
    private String destinationStop;
    private int price;
    private LocalDateTime boughtTime;
    private Boolean isValid;
    private LocalDateTime validUntil;

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public String getPassengerPhoneNumber() {
        return passengerPhoneNumber;
    }

    public void setPassengerPhoneNumber(String passengerPhoneNumber) {
        this.passengerPhoneNumber = passengerPhoneNumber;
    }

    public int getBusId() {
        return busId;
    }

    public void setBusId(int busId) {
        this.busId = busId;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public String getSourceStop() {
        return sourceStop;
    }

    public void setSourceStop(String sourceStop) {
        this.sourceStop = sourceStop;
    }

    public String getDestinationStop() {
        return destinationStop;
    }

    public void setDestinationStop(String destinationStop) {
        this.destinationStop = destinationStop;
    }
    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price=price;
    }

    public LocalDateTime getBoughtTime() {
        return boughtTime;
    }

    public void setBoughtTime(LocalDateTime boughtTime) {
        this.boughtTime = boughtTime;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }
    public Boolean getIsValid() {
        return isValid;
    }
    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }
}
