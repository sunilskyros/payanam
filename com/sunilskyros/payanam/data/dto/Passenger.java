package com.sunilskyros.payanam.data.dto;

public class Passenger {

    private String name;
    private long phoneNumber;
    private String password;
    private Role role;
    private Status status;

    public enum Role{
        PASSENGER,TICKET_COLLECTOR
    }

    public enum Status{
        ACTIVE,IN_ACTIVE
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    public long getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber= phoneNumber;
    }

}
