package com.minhky.go4.model;

public class Account {
    String role;
    String agent;
    String email;
    String deliveryTime;
    String active;
    String database;
    String hotline;

    public Account() {
    }

    public Account(String agent, String deliveryTime) {
        this.agent = agent;
        this.deliveryTime = deliveryTime;
    }

    public Account(String Active, String Database, String Hotline) {
        this.active = Active;
        this.database = Database;
        this.hotline = Hotline;
    }

    public String getRole() {
        return role;
    }

    public String getAgent() {
        return agent;
    }

    public String getEmail() {
        return email;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public String getActive() {
        return active;
    }

    public String getDatabase() {
        return database;
    }

    public String getHotline() {
        return hotline;
    }
}
