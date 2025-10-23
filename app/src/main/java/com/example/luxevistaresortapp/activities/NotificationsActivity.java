package com.example.luxevistaresortapp.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.luxevistaresortapp.R;
import com.example.luxevistaresortapp.adapters.NotificationAdapter;
import com.example.luxevistaresortapp.models.Notification;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {
    private RecyclerView notificationsRecyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notificationList = new ArrayList<>();
    private FirebaseFirestore db;
    private LinearLayout emptyStateLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        db = FirebaseFirestore.getInstance();
        
        notificationsRecyclerView = findViewById(R.id.notificationsRecyclerView);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new NotificationAdapter(this, notificationList, new NotificationAdapter.OnNotificationActionListener() {
            @Override
            public void onEdit(Notification notification) {
                // Not used in user view
            }
            
            @Override
            public void onDelete(Notification notification) {
                // Not used in user view
            }
            
            @Override
            public void onToggleActive(Notification notification) {
                // Not used in user view
            }
            
            @Override
            public void onView(Notification notification) {
                showNotificationDetails(notification);
            }
        }, false); // false for user view
        
        notificationsRecyclerView.setAdapter(adapter);
        fetchNotifications();
    }

    private void fetchNotifications() {
        Timestamp now = Timestamp.now();
        String currentUserId = com.example.luxevistaresortapp.utils.FirebaseHelper.getInstance().getCurrentUserId();
        String currentUserRole = "guest"; // TODO: fetch from user profile if you have roles
        db.collection("notifications")
                .whereEqualTo("isActive", true)
                .whereGreaterThan("expiresAt", now)
                .orderBy("expiresAt", Query.Direction.DESCENDING)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    notificationList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Notification notification = document.toObject(Notification.class);
                        if (notification != null) {
                            notification.setId(document.getId());
                            // Show if recipientType == all
                            if ("all".equals(notification.getRecipientType())) {
                                notificationList.add(notification);
                            } else if ("user".equals(notification.getRecipientType()) &&
                                    currentUserId != null &&
                                    currentUserId.equals(notification.getRecipientId())) {
                                notificationList.add(notification);
                            } else if ("group".equals(notification.getRecipientType()) &&
                                    currentUserRole != null &&
                                    currentUserRole.equals(notification.getRecipientId())) {
                                notificationList.add(notification);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                    updateEmptyState();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch notifications: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    updateEmptyState();
                });
    }

    private void updateEmptyState() {
        if (notificationList.isEmpty()) {
            notificationsRecyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            notificationsRecyclerView.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }
    }

    private void showNotificationDetails(Notification notification) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(notification.getTitle());
        builder.setMessage(notification.getMessage());
        
        // Add type and date information
        String details = "Type: " + notification.getTypeDisplayName() + "\n";
        if (notification.getCreatedAt() != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", java.util.Locale.getDefault());
            details += "Posted: " + sdf.format(notification.getCreatedAt().toDate()) + "\n";
        }
        if (notification.getExpiresAt() != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", java.util.Locale.getDefault());
            details += "Expires: " + sdf.format(notification.getExpiresAt().toDate());
        }
        
        builder.setMessage(notification.getMessage() + "\n\n" + details);
        builder.setPositiveButton("Close", null);
        
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchNotifications();
    }
} 