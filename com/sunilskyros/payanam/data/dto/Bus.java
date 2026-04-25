package com.sunilskyros.payanam.data.dto;

import java.time.LocalTime;
import java.util.List;

public class Bus {

    private int id;
    private String name;
    private List<Stop> stops;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public List<Stop> getStops(){
        return stops;
    }
    public void setStop(List<Stop> stops){
        this.stops = stops;
    }
}
