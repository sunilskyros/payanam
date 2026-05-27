package com.sunilskyros.payanam.data.dto;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "stops")
public class Stop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer dbId;

    @Column(name = "stop_id_seq")
    private int id;

    @Column(name = "bus_id")
    private int busId;

    @Column(name = "stop_name", length = 50)
    private String stopName;

    @Column(name = "updated_time")
    private LocalTime currentTime;

    @Column(name = "current_stop")
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

    public LocalTime getUpdatedTime() {
        return currentTime;
    }

    public void setUpdatedTime(LocalTime currentTime) {
        this.currentTime = currentTime;
    }

    public void setCurrentStop(Boolean currentStop) {
        this.currentStop = currentStop;
    }

    public Boolean getCurrentStop() {
        return currentStop;
    }

    public Integer getDbId() {
        return dbId;
    }

    public void setDbId(Integer dbId) {
        this.dbId = dbId;
    }
}
