package com.example.luxevistaresortapp.models;

public class ServiceReservation {
    private String userId;
    private String serviceId;
    private String serviceName;
    private String date;
    private long timestamp;

    public ServiceReservation() {}

    public ServiceReservation(String userId, String serviceId, String serviceName, String date, long timestamp) {
        this.userId = userId;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.date = date;
        this.timestamp = timestamp;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
} 