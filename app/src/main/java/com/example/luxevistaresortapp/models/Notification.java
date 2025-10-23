package com.example.luxevistaresortapp.models;

import com.google.firebase.Timestamp;

public class Notification {
    private String id;
    private String title;
    private String message;
    private String type; // "event", "discount", "service_update", "general"
    private boolean isActive;
    private Timestamp createdAt;
    private Timestamp expiresAt;
    private String imageUrl;
    private String targetAudience; // "all", "guests", "members", "vip"
    private String recipientType; // "all", "group", "user"
    private String recipientId;   // userId or group name (optional)

    public Notification() {
        // Required empty constructor for Firestore
    }

    public Notification(String id, String title, String message, String type, boolean isActive, 
                       Timestamp createdAt, Timestamp expiresAt, String imageUrl, String targetAudience) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.type = type;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.imageUrl = imageUrl;
        this.targetAudience = targetAudience;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Timestamp expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public boolean isExpired() {
        if (expiresAt == null) return false;
        return expiresAt.toDate().before(new java.util.Date());
    }

    public String getTypeDisplayName() {
        switch (type) {
            case "event": return "Special Event";
            case "discount": return "Discount";
            case "service_update": return "Service Update";
            case "general": return "General";
            default: return "Notification";
        }
    }

    public int getTypeIcon() {
        switch (type) {
            case "event": return android.R.drawable.ic_menu_myplaces;
            case "discount": return android.R.drawable.ic_menu_agenda;
            case "service_update": return android.R.drawable.ic_menu_info_details;
            case "general": return android.R.drawable.ic_menu_help;
            default: return android.R.drawable.ic_menu_help;
        }
    }

    public String getRecipientType() {
        return recipientType;
    }
    public void setRecipientType(String recipientType) {
        this.recipientType = recipientType;
    }
    public String getRecipientId() {
        return recipientId;
    }
    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }
} 