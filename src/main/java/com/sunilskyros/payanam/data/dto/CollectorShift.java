package com.sunilskyros.payanam.data.dto;

import jakarta.persistence.*;

@Entity
@Table(name = "collector_shifts")
public class CollectorShift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "collector_phone", length = 15)
    private String collectorPhone;

    @Column(name = "bus_id")
    private int busId;

    @Column(name = "bus_name", length = 50)
    private String busName;

    @Column(name = "shift_date", length = 20)
    private String shiftDate;

    @Column(name = "start_time", length = 20)
    private String startTime;

    @Column(name = "end_time", length = 20)
    private String endTime;

    @Column(name = "tickets_checked")
    private int ticketsChecked;

    @Column(name = "status", length = 20)
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCollectorPhone() {
        return collectorPhone;
    }

    public void setCollectorPhone(String collectorPhone) {
        this.collectorPhone = collectorPhone;
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

    public String getShiftDate() {
        return shiftDate;
    }

    public void setShiftDate(String shiftDate) {
        this.shiftDate = shiftDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getTicketsChecked() {
        return ticketsChecked;
    }

    public void setTicketsChecked(int ticketsChecked) {
        this.ticketsChecked = ticketsChecked;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
