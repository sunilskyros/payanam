package com.sunilskyros.payanam.data.dto;

import jakarta.persistence.*;

@Entity
@Table(name = "passengers")
public class Passenger {

    @Column(name = "name", length = 50)
    private String name;

    @Id
    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20)
    private Role role;

    @Column(name = "password", length = 60)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private Status status;

    public enum Role {
        PASSENGER, TICKETCOLLECTOR, ADMIN
    }

    public enum Status {
        ACTIVE, INACTIVE
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
