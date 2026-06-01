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

    @Column(name = "bought_time")
    private LocalDateTime boughtTime;

    @Column(name = "is_valid")
    private Boolean isValid;

    @Column(name = "valid_until")
    private LocalDateTime validUntil;

    @Column(name = "booking_reference", length = 20)
    private String bookingReference;

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

    public String getBookingReference() {
        return bookingReference;
    }

    public void setBookingReference(String bookingReference) {
        this.bookingReference = bookingReference;
    }

    @Transient
    public String getSignature() {
        try {
            String rawData = ticketId + ":" + passengerPhoneNumber + ":" + sourceStop + ":" + destinationStop + ":" + bookingReference + ":PayanamSecureSalt2026";
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawData.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString().substring(0, 16); // 16-character secure hash signature
        } catch (Exception ex) {
            return "DEFAULT_SIGNATURE";
        }
    }
}
