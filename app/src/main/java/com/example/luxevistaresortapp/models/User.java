package com.example.luxevistaresortapp.models;

public class User {
    private String id;
    private String name;
    private String email;
    private String preferences;
    private String travelDates;
    private String role;

    public User() {
        // Default constructor for Firestore
    }

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.preferences = "";
        this.travelDates = "";
    }

    public User(String id, String name, String email, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.preferences = "";
        this.travelDates = "";
    }

    public User(String id, String name, String email, String preferences, String travelDates) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.preferences = preferences;
        this.travelDates = travelDates;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    public String getTravelDates() {
        return travelDates;
    }

    public void setTravelDates(String travelDates) {
        this.travelDates = travelDates;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}