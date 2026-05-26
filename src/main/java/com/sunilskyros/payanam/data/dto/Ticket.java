package com.sunilskyros.payanam.data.dto;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private int ticketId;

    @Column(name = "passenger_phone_number", length = 15)
    private String passengerPhoneNumber;

    @Column(name = "bus_id")
    private int busId;

    @Column(name = "bus_name", length = 50)
    private String busName;

    @Column(name = "source_stop", length = 50)
    private String sourceStop;

    @Column(name = "destination_stop", length = 50)
    private String destinationStop;

    @Column(name = "price")
    private int price;

    @Column(name = "distance")
    private Double distance;

    @Column(name = "fare")
    private Double fare;

    @Column(name = "bought_time")
    private LocalDateTime boughtTime;

    @Column(name = "is_valid")
    private Boolean isValid;

    @Column(name = "valid_until")
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
        this.price = price;
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

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getFare() {
        return fare;
    }

    public void setFare(Double fare) {
        this.fare = fare;
        if (fare != null) {
            this.price = (int) Math.round(fare);
        }
    }
}
