package com.sunilskyros.payanam.data.dto;

public class Stop {

    private int id;
    private int busId;
    private String stopName;
    private int order;
    private Boolean currentStop;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBusId() {
        return busId;
    }

    public void setBusId(int busId) {
        this.busId = busId;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setCurrentStop(Boolean currentStop){
        this.currentStop=currentStop;
    }

    public Boolean getCurrentStop(){
        return currentStop;
    }

}
