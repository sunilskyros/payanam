package com.sunilskyros.payanam.data.dto;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bus_location_history")
public class BusLocationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bus_id", nullable = false)
    private int busId;

    @Column(name = "current_stop_id")
    private Integer currentStopId;

    @Column(name = "current_stop_name", length = 50)
    private String currentStopName;

    @Column(name = "updated_time", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "updated_by", length = 20)
    private String updatedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getBusId() {
        return busId;
    }

    public void setBusId(int busId) {
        this.busId = busId;
    }

    public Integer getCurrentStopId() {
        return currentStopId;
    }

    public void setCurrentStopId(Integer currentStopId) {
        this.currentStopId = currentStopId;
    }

    public String getCurrentStopName() {
        return currentStopName;
    }

    public void setCurrentStopName(String currentStopName) {
        this.currentStopName = currentStopName;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
