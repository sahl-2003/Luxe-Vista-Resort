package com.example.luxevistaresortapp.utils;

import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class FirestoreInitializer {
    private static final String TAG = "FirestoreInitializer";
    private FirebaseFirestore db;

    public FirestoreInitializer() {
        db = FirebaseFirestore.getInstance();
    }

    public void initializeAllData() {
        addInitialRooms();
        addInitialAttractions();
        addInitialOffers();
        addInitialServices();
        addInitialNotifications();
        Log.d(TAG, "Firestore initialization completed");
    }

    public void addInitialRooms() {
        // Room 1: Ocean Suite
        Map<String, Object> room1 = new HashMap<>();
        room1.put("id", "room1");
        room1.put("name", "Ocean Suite");
        room1.put("type", "Ocean View Suite");
        room1.put("price", 300.00); // Updated price for realism
        room1.put("imageUrl", "file:///android_res/drawable/suite1");
        room1.put("description", "A luxurious suite with a stunning ocean view, featuring a private balcony and premium amenities.");

        db.collection("rooms").document("room1").get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        db.collection("rooms").document("room1").set(room1)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Room added successfully: Ocean Suite"))
                                .addOnFailureListener(e -> Log.e(TAG, "Error adding room (Ocean Suite): " + e.getMessage()));
                    } else {
                        Log.d(TAG, "Room already exists: Ocean Suite");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error checking room existence (Ocean Suite): " + e.getMessage()));

        // Room 2: Deluxe Room
        Map<String, Object> room2 = new HashMap<>();
        room2.put("id", "room2");
        room2.put("name", "Deluxe Room");
        room2.put("type", "Deluxe Room");
        room2.put("price", 200.00); // Updated price
        room2.put("imageUrl", "file:///android_res/drawable/room3");
        room2.put("description", "A spacious deluxe room with modern amenities, ideal for comfort and relaxation.");

        db.collection("rooms").document("room2").get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        db.collection("rooms").document("room2").set(room2)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Room added successfully: Deluxe Room"))
                                .addOnFailureListener(e -> Log.e(TAG, "Error adding room (Deluxe Room): " + e.getMessage()));
                    } else {
                        Log.d(TAG, "Room already exists: Deluxe Room");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error checking room existence (Deluxe Room): " + e.getMessage()));

        // Room 3: Standard Room
        Map<String, Object> room3 = new HashMap<>();
        room3.put("id", "room3");
        room3.put("name", "Standard Room");
        room3.put("type", "Standard Room");
        room3.put("price", 150.00); // Updated price
        room3.put("imageUrl", "file:///android_res/drawable/room4");
        room3.put("description", "A cozy standard room with essential amenities, perfect for budget travelers.");

        db.collection("rooms").document("room3").get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        db.collection("rooms").document("room3").set(room3)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Room added successfully: Standard Room"))
                                .addOnFailureListener(e -> Log.e(TAG, "Error adding room (Standard Room): " + e.getMessage()));
                    } else {
                        Log.d(TAG, "Room already exists: Standard Room");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error checking room existence (Standard Room): " + e.getMessage()));
    }

    public void addInitialAttractions() {
        // Attraction 1: Waikiki Beach
        Map<String, Object> attraction1 = new HashMap<>();
        attraction1.put("id", "attraction1");
        attraction1.put("name", "Waikiki Beach");
        attraction1.put("description", "A famous beach known for its golden sands, surfing opportunities, and vibrant atmosphere.");
        attraction1.put("imageUrl", "file:///android_res/drawable/beach1");

        db.collection("attractions").document("attraction1").get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        db.collection("attractions").document("attraction1").set(attraction1)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Attraction added successfully: Waikiki Beach"))
                                .addOnFailureListener(e -> Log.e(TAG, "Error adding attraction (Waikiki Beach): " + e.getMessage()));
                    } else {
                        Log.d(TAG, "Attraction already exists: Waikiki Beach");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error checking attraction existence (Waikiki Beach): " + e.getMessage()));

        // Attraction 2: Diamond Head Hike
        Map<String, Object> attraction2 = new HashMap<>();
        attraction2.put("id", "attraction2");
        attraction2.put("name", "Diamond Head Hike");
        attraction2.put("description", "A scenic hike to the summit of Diamond Head with panoramic views of Honolulu and the Pacific Ocean.");
        attraction2.put("imageUrl", "file:///android_res/drawable/diamond_head");

        db.collection("attractions").document("attraction2").get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        db.collection("attractions").document("attraction2").set(attraction2)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Attraction added successfully: Diamond Head Hike"))
                                .addOnFailureListener(e -> Log.e(TAG, "Error adding attraction (Diamond Head Hike): " + e.getMessage()));
                    } else {
                        Log.d(TAG, "Attraction already exists: Diamond Head Hike");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error checking attraction existence (Diamond Head Hike): " + e.getMessage()));

        // Attraction 3: Hanauma Bay Snorkeling
        Map<String, Object> attraction3 = new HashMap<>();
        attraction3.put("id", "attraction3");
        attraction3.put("name", "Hanauma Bay Snorkeling");
        attraction3.put("description", "Snorkel in the crystal-clear waters of Hanauma Bay, a marine conservation area teeming with tropical fish.");
        attraction3.put("imageUrl", "file:///android_res/drawable/hanauma_bay");

        db.collection("attractions").document("attraction3").get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        db.collection("attractions").document("attraction3").set(attraction3)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Attraction added successfully: Hanauma Bay Snorkeling"))
                                .addOnFailureListener(e -> Log.e(TAG, "Error adding attraction (Hanauma Bay Snorkeling): " + e.getMessage()));
                    } else {
                        Log.d(TAG, "Attraction already exists: Hanauma Bay Snorkeling");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error checking attraction existence (Hanauma Bay Snorkeling): " + e.getMessage()));
    }

    public void addInitialOffers() {
        // Offer 1: Summer Discount
        Map<String, Object> offer1 = new HashMap<>();
        offer1.put("id", "offer1");
        offer1.put("title", "Summer Discount");
        offer1.put("description", "Get 20% off on all bookings this summer! Valid until August 31, 2025.");
        offer1.put("active", true);

        db.collection("offers").document("offer1").get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        db.collection("offers").document("offer1").set(offer1)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Offer added successfully: Summer Discount"))
                                .addOnFailureListener(e -> Log.e(TAG, "Error adding offer (Summer Discount): " + e.getMessage()));
                    } else {
                        Log.d(TAG, "Offer already exists: Summer Discount");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error checking offer existence (Summer Discount): " + e.getMessage()));

        // Offer 2: Spa Package Deal
        Map<String, Object> offer2 = new HashMap<>();
        offer2.put("id", "offer2");
        offer2.put("title", "Spa Package Deal");
        offer2.put("description", "Book a spa massage and get a complimentary facial! Valid until June 30, 2025.");
        offer2.put("active", true);

        db.collection("offers").document("offer2").get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        db.collection("offers").document("offer2").set(offer2)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Offer added successfully: Spa Package Deal"))
                                .addOnFailureListener(e -> Log.e(TAG, "Error adding offer (Spa Package Deal): " + e.getMessage()));
                    } else {
                        Log.d(TAG, "Offer already exists: Spa Package Deal");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error checking offer existence (Spa Package Deal): " + e.getMessage()));
    }

    public void addInitialServices() {
        // Service 1: Spa Massage
        Map<String, Object> service1 = new HashMap<>();
        service1.put("id", "service1");
        service1.put("name", "Spa Massage");
        service1.put("type", "Spa");
        service1.put("price", 120.00); // Updated price
        service1.put("description", "A 60-minute relaxing massage session at our luxury spa, using aromatherapy oils.");
        service1.put("imageUrl", "file:///android_res/drawable/spamassage");

        db.collection("services").document("service1").get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        db.collection("services").document("service1").set(service1)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Service added successfully: Spa Massage"))
                                .addOnFailureListener(e -> Log.e(TAG, "Error adding service (Spa Massage): " + e.getMessage()));
                    } else {
                        Log.d(TAG, "Service already exists: Spa Massage");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error checking service existence (Spa Massage): " + e.getMessage()));

        // Service 2: Guided Tour
        Map<String, Object> service2 = new HashMap<>();
        service2.put("id", "service2");
        service2.put("name", "Guided Tour");
        service2.put("type", "Tour");
        service2.put("price", 60.00); // Updated price
        service2.put("description", "A half-day guided tour of local attractions, including transportation and a knowledgeable guide.");
        service2.put("imageUrl", "file:///android_res/drawable/guidedtour");

        db.collection("services").document("service2").get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        db.collection("services").document("service2").set(service2)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Service added successfully: Guided Tour"))
                                .addOnFailureListener(e -> Log.e(TAG, "Error adding service (Guided Tour): " + e.getMessage()));
                    } else {
                        Log.d(TAG, "Service already exists: Guided Tour");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error checking service existence (Guided Tour): " + e.getMessage()));

        // Service 3: Fine Dining
        Map<String, Object> service3 = new HashMap<>();
        service3.put("id", "service3");
        service3.put("name", "Fine Dining");
        service3.put("type", "Dining");
        service3.put("price", 90.00); // Updated price
        service3.put("description", "A gourmet 3-course dining experience at our signature restaurant, featuring local flavors.");
        service3.put("imageUrl", "file:///android_res/drawable/finedining");

        db.collection("services").document("service3").get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        db.collection("services").document("service3").set(service3)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Service added successfully: Fine Dining"))
                                .addOnFailureListener(e -> Log.e(TAG, "Error adding service (Fine Dining): " + e.getMessage()));
                    } else {
                        Log.d(TAG, "Service already exists: Fine Dining");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error checking service existence (Fine Dining): " + e.getMessage()));
    }

    public void addInitialNotifications() {
        // Sample notification 1: Special Event
        Map<String, Object> notification1 = new HashMap<>();
        notification1.put("id", "notification1");
        notification1.put("title", "Summer Beach Party");
        notification1.put("message", "Join us for an amazing summer beach party this Saturday! Live music, delicious cocktails, and stunning ocean views. Don't miss this exclusive event for our guests.");
        notification1.put("type", "event");
        notification1.put("isActive", true);
        notification1.put("createdAt", com.google.firebase.Timestamp.now());
        notification1.put("expiresAt", new com.google.firebase.Timestamp(new java.util.Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000))); // 7 days from now
        notification1.put("targetAudience", "all");

        db.collection("notifications").document("notification1").get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        db.collection("notifications").document("notification1").set(notification1)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Notification added successfully: Summer Beach Party"))
                                .addOnFailureListener(e -> Log.e(TAG, "Error adding notification (Summer Beach Party): " + e.getMessage()));
                    } else {
                        Log.d(TAG, "Notification already exists: Summer Beach Party");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error checking notification existence (Summer Beach Party): " + e.getMessage()));

        // Sample notification 2: Discount
        Map<String, Object> notification2 = new HashMap<>();
        notification2.put("id", "notification2");
        notification2.put("title", "Weekend Spa Special");
        notification2.put("message", "Enjoy 25% off all spa treatments this weekend! Book your relaxing massage or rejuvenating facial and save big. Limited time offer.");
        notification2.put("type", "discount");
        notification2.put("isActive", true);
        notification2.put("createdAt", com.google.firebase.Timestamp.now());
        notification2.put("expiresAt", new com.google.firebase.Timestamp(new java.util.Date(System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000))); // 3 days from now
        notification2.put("targetAudience", "all");

        db.collection("notifications").document("notification2").get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        db.collection("notifications").document("notification2").set(notification2)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Notification added successfully: Weekend Spa Special"))
                                .addOnFailureListener(e -> Log.e(TAG, "Error adding notification (Weekend Spa Special): " + e.getMessage()));
                    } else {
                        Log.d(TAG, "Notification already exists: Weekend Spa Special");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error checking notification existence (Weekend Spa Special): " + e.getMessage()));

        // Sample notification 3: Service Update
        Map<String, Object> notification3 = new HashMap<>();
        notification3.put("id", "notification3");
        notification3.put("title", "Pool Maintenance Update");
        notification3.put("message", "Our main pool will be temporarily closed for maintenance on Tuesday from 10 AM to 2 PM. The infinity pool and spa will remain open. We apologize for any inconvenience.");
        notification3.put("type", "service_update");
        notification3.put("isActive", true);
        notification3.put("createdAt", com.google.firebase.Timestamp.now());
        notification3.put("expiresAt", new com.google.firebase.Timestamp(new java.util.Date(System.currentTimeMillis() + 5 * 24 * 60 * 60 * 1000))); // 5 days from now
        notification3.put("targetAudience", "all");

        db.collection("notifications").document("notification3").get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        db.collection("notifications").document("notification3").set(notification3)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Notification added successfully: Pool Maintenance Update"))
                                .addOnFailureListener(e -> Log.e(TAG, "Error adding notification (Pool Maintenance Update): " + e.getMessage()));
                    } else {
                        Log.d(TAG, "Notification already exists: Pool Maintenance Update");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error checking notification existence (Pool Maintenance Update): " + e.getMessage()));
    }
}