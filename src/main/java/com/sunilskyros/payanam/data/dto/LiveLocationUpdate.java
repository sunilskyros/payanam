package com.sunilskyros.payanam.data.dto;

import java.io.Serializable;
import java.util.List;

public class LiveLocationUpdate implements Serializable {
    private static final long serialVersionUID = 1L;

    private int busId;
    private String busName;
    private Double latitude;
    private Double longitude;
    private Integer currentStopSeq;
    private String currentStopName;
    private String updatedTime;
    private String busStatus; // ACTIVE, DELAYED, COMPLETED
    private int passengerCount;
    private List<Stop> stops;

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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getCurrentStopSeq() {
        return currentStopSeq;
    }

    public void setCurrentStopSeq(Integer currentStopSeq) {
        this.currentStopSeq = currentStopSeq;
    }

    public String getCurrentStopName() {
        return currentStopName;
    }

    public void setCurrentStopName(String currentStopName) {
        this.currentStopName = currentStopName;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getBusStatus() {
        return busStatus;
    }

    public void setBusStatus(String busStatus) {
        this.busStatus = busStatus;
    }

    public int getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(int passengerCount) {
        this.passengerCount = passengerCount;
    }

    public List<Stop> getStops() {
        return stops;
    }

    public void setStops(List<Stop> stops) {
        this.stops = stops;
    }
}
