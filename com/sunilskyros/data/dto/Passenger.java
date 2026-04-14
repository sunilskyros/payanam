package com.sunilskyros.data.dto;

public class Passenger {

    private int id;
    private String password;
    private String name;
    private Role role;
    private Status status;

    public enum Role{
        PASSENGER,TICKET_COLLECTOR
    }

    public enum Status{
        ACTIVE,IN_ACTIVE
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

}
