package com.example.luxevistaresortapp.utils;

import android.content.Context;
import android.util.Log;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.Date;

public class NotificationService {
    private static final String TAG = "NotificationService";
    private static FirebaseFirestore db;
    private static NotificationService instance;

    private NotificationService() {
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    public interface NotificationCallback {
        void onNotificationCount(int count);
        void onError(String error);
    }

    public void checkForNewNotifications(Context context, NotificationCallback callback) {
        // Get current timestamp
        Timestamp now = Timestamp.now();
        
        db.collection("notifications")
                .whereEqualTo("isActive", true)
                .whereGreaterThan("expiresAt", now)
                .orderBy("expiresAt", Query.Direction.DESCENDING)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int count = queryDocumentSnapshots.size();
                    Log.d(TAG, "Found " + count + " active notifications");
                    callback.onNotificationCount(count);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error checking notifications: " + e.getMessage());
                    callback.onError(e.getMessage());
                });
    }

    public void sendTestNotification(Context context) {
        // Send a test notification to verify the system works
        NotificationUtil.sendNotification(context, 
            "Test Notification", 
            "This is a test notification to verify the notification system is working properly.",
            "general");
    }

    public void sendWelcomeNotification(Context context) {
        // Send a welcome notification to new users
        NotificationUtil.sendNotification(context,
            "Welcome to LuxeVista Resort!",
            "Thank you for choosing LuxeVista Resort. We hope you enjoy your stay and don't forget to check our special events and offers!",
            "general");
    }
} 