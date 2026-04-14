package com.sunilskyros.payanam.data.dto;

import java.time.LocalTime;

public class Bus {

    private int id;
    private Route route;
    private String startingStop;
    private String endingStop;
    private LocalTime startingTime;
    private LocalTime endingTime;
    private String stops;
    private String curStop;

    public enum Route{
        UP_ROUTE,DOWN_ROUTE
    }

    public String getCurStop() {
        return curStop;
    }

    public void setCurStop(String curStop) {
        this.curStop = curStop;
    }
    public Route getRoute() {
        return route;
    }

    public void setRole(Route route) {
        this.route = route;
    }

    public String getStartingStop() {
        return startingStop;
    }

    public void setStartingStop(String startStop) {
        this.startingStop = startStop;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEndingStop() {
        return endingStop;
    }

    public void setEndingStop(String endingStop) {
        this.endingStop = endingStop;
    }

    public LocalTime getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(LocalTime startingTime) {
        this.startingTime = startingTime;
    }

    public LocalTime getEndingTime() {
        return endingTime;
    }

    public void setEndingTime(LocalTime endingTime) {
        this.endingTime = endingTime;
    }

    public String getStops() {
        return stops;
    }

    public void setStops(String stops) {
        this.stops = stops;
    }
}
