

package com.example.luxevistaresortapp.models;

public class Room {
    private String id;
    private String name;
    private String type;
    private double price;
    private String imageUrl;
    private String description;
    private boolean available = true;

    public Room() {
    }

    public Room(String id, String name, String type, double price, String imageUrl, String description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}