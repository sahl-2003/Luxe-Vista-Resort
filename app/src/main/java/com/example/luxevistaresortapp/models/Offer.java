package com.example.luxevistaresortapp.models;

public class Offer {
    private String id;
    private String title;
    private String description;
    private boolean active;

    public Offer() {
    }

    public Offer(String id, String title, String description, boolean active) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.active = active;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}