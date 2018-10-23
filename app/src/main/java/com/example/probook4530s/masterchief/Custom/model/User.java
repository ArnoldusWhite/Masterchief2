package com.example.probook4530s.masterchief.Custom.model;

public class User {
    private String username;
    private String pwd;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public User(String username, String pwd, String id) {
        this.username = username;
        this.pwd = pwd;
        this.id=id;
    }

    public User() {
        this.username = "";
        this.pwd = "";
        this.id = "";
    }
}
