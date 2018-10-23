package com.example.probook4530s.masterchief.Custom.model;

public class Receipt {
    private String illustration;
    private String name;
    private String duration;
    private String description;
    private String guest;
    private String user;
    private String id;

    public String getIllustration() {
        return illustration;
    }

    public void setIllustration(String illustration) {
        this.illustration = illustration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGuest() {
        return guest;
    }

    public void setGuest(String guest) {
        this.guest = guest;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Receipt(String name, String duration, String description, String guest, String user, String id, String illustration) {
        this.name = name;
        this.duration = duration;
        this.description = description;
        this.guest = guest;
        this.user = user;
        this.id = id;
        this.illustration = illustration;
    }

    public Receipt(String name, String duration, String description, String guest, String id, String illustration) {
        this.name = name;
        this.duration = duration;
        this.description = description;
        this.guest = guest;
        this.id = id;
        this.illustration = illustration;
    }

    public Receipt(String name, String duration, String description, String guest, String id) {
        this.name = name;
        this.duration = duration;
        this.description = description;
        this.guest = guest;
        this.id = id;
    }

    public Receipt(){}
}
