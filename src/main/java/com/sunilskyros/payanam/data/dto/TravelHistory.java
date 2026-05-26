package com.sunilskyros.payanam.data.dto;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "travel_histories")
public class TravelHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "passenger_phone", length = 15)
    private String passengerPhone;

    @Column(name = "bus_id")
    private int busId;

    @Column(name = "bus_name", length = 50)
    private String busName;

    @Column(name = "source_stop", length = 50)
    private String sourceStop;

    @Column(name = "destination_stop", length = 50)
    private String destinationStop;

    @Column(name = "travel_date")
    private LocalDateTime travelDate;

    @Column(name = "distance")
    private Double distance;

    @Column(name = "fare")
    private Double fare;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassengerPhone() {
        return passengerPhone;
    }

    public void setPassengerPhone(String passengerPhone) {
        this.passengerPhone = passengerPhone;
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

    public LocalDateTime getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(LocalDateTime travelDate) {
        this.travelDate = travelDate;
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
    }
}
