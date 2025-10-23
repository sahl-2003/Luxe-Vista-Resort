package com.example.luxevistaresortapp.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.luxevistaresortapp.R;
import com.example.luxevistaresortapp.adapters.NotificationAdapter;
import com.example.luxevistaresortapp.models.Notification;
import com.example.luxevistaresortapp.utils.FirebaseHelper;
import com.example.luxevistaresortapp.utils.NotificationUtil;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ManageNotificationsActivity extends AppCompatActivity {
    private RecyclerView notificationsRecyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notificationList = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_notifications);

        db = FirebaseFirestore.getInstance();
        firebaseHelper = FirebaseHelper.getInstance();
        
        notificationsRecyclerView = findViewById(R.id.notificationsRecyclerView);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new NotificationAdapter(this, notificationList, new NotificationAdapter.OnNotificationActionListener() {
            @Override
            public void onEdit(Notification notification) {
                showNotificationDialog(notification);
            }
            
            @Override
            public void onDelete(Notification notification) {
                deleteNotification(notification);
            }
            
            @Override
            public void onToggleActive(Notification notification) {
                toggleNotificationActive(notification);
            }
            
            @Override
            public void onView(Notification notification) {
                // Not used in admin view
            }
        }, true);
        
        notificationsRecyclerView.setAdapter(adapter);

        findViewById(R.id.addNotificationButton).setOnClickListener(v -> showNotificationDialog(null));
        fetchNotifications();
    }

    private void fetchNotifications() {
        db.collection("notifications")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    notificationList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Notification notification = document.toObject(Notification.class);
                        if (notification != null) {
                            notification.setId(document.getId());
                            notificationList.add(notification);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch notifications: " + e.getMessage(), 
                                 Toast.LENGTH_SHORT).show();
                });
    }

    private void showNotificationDialog(Notification notification) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_edit_notification);
        dialog.setCancelable(true);

        EditText titleEdit = dialog.findViewById(R.id.titleEditText);
        EditText messageEdit = dialog.findViewById(R.id.messageEditText);
        Spinner typeSpinner = dialog.findViewById(R.id.typeSpinner);
        Switch activeSwitch = dialog.findViewById(R.id.activeSwitch);
        Button expiryDateButton = dialog.findViewById(R.id.expiryDateButton);
        Button expiryTimeButton = dialog.findViewById(R.id.expiryTimeButton);
        Button saveButton = dialog.findViewById(R.id.saveButton);
        Button cancelButton = dialog.findViewById(R.id.cancelButton);
        Spinner recipientTypeSpinner = dialog.findViewById(R.id.recipientTypeSpinner);
        Spinner recipientIdSpinner = dialog.findViewById(R.id.recipientIdSpinner);
        TextView recipientIdLabel = dialog.findViewById(R.id.recipientIdLabel);

        // Setup spinners
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this, 
                R.array.notification_types, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);

        // Setup recipient type spinner
        ArrayAdapter<CharSequence> recipientTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.recipient_types, android.R.layout.simple_spinner_item);
        recipientTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recipientTypeSpinner.setAdapter(recipientTypeAdapter);

        // Setup user group spinner
        ArrayAdapter<CharSequence> userGroupAdapter = ArrayAdapter.createFromResource(this,
                R.array.user_groups, android.R.layout.simple_spinner_item);
        userGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Setup user list spinner (for specific user)
        List<String> userIdList = new ArrayList<>();
        ArrayAdapter<String> userIdAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userIdList);
        userIdAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Populate userIdList from Firestore
        db.collection("users").get().addOnSuccessListener(queryDocumentSnapshots -> {
            userIdList.clear();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                String email = doc.getString("email");
                String uid = doc.getId();
                if (email != null) {
                    userIdList.add(email + " (" + uid + ")");
                } else {
                    userIdList.add(uid);
                }
            }
            userIdAdapter.notifyDataSetChanged();
        });

        // Show/hide recipientId spinner based on recipient type
        recipientTypeSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selected = recipientTypeSpinner.getSelectedItem().toString();
                if (selected.equals("All Users")) {
                    recipientIdLabel.setVisibility(View.GONE);
                    recipientIdSpinner.setVisibility(View.GONE);
                } else if (selected.equals("User Group")) {
                    recipientIdLabel.setText("User Group");
                    recipientIdLabel.setVisibility(View.VISIBLE);
                    recipientIdSpinner.setAdapter(userGroupAdapter);
                    recipientIdSpinner.setVisibility(View.VISIBLE);
                } else if (selected.equals("Specific User")) {
                    recipientIdLabel.setText("User");
                    recipientIdLabel.setVisibility(View.VISIBLE);
                    recipientIdSpinner.setAdapter(userIdAdapter);
                    recipientIdSpinner.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // Set current values if editing
        if (notification != null) {
            titleEdit.setText(notification.getTitle());
            messageEdit.setText(notification.getMessage());
            
            // Set spinner positions
            String[] types = getResources().getStringArray(R.array.notification_types);
            for (int i = 0; i < types.length; i++) {
                if (types[i].equalsIgnoreCase(notification.getType())) {
                    typeSpinner.setSelection(i);
                    break;
                }
            }
            
            activeSwitch.setChecked(notification.isActive());

            // Set recipient type and ID if editing
            String recipientType = notification.getRecipientType();
            String recipientId = notification.getRecipientId();

            if (recipientType != null) {
                for (int i = 0; i < recipientTypeAdapter.getCount(); i++) {
                    if (recipientTypeAdapter.getItem(i).toString().equalsIgnoreCase(recipientType)) {
                        recipientTypeSpinner.setSelection(i);
                        break;
                    }
                }

                if (recipientType.equals("group")) {
                    recipientIdLabel.setText("User Group");
                    recipientIdLabel.setVisibility(View.VISIBLE);
                    for (int i = 0; i < userGroupAdapter.getCount(); i++) {
                        if (userGroupAdapter.getItem(i).toString().equalsIgnoreCase(recipientId)) {
                            recipientIdSpinner.setSelection(i);
                            break;
                        }
                    }
                    recipientIdSpinner.setVisibility(View.VISIBLE);
                } else if (recipientType.equals("user")) {
                    recipientIdLabel.setText("User");
                    recipientIdLabel.setVisibility(View.VISIBLE);
                    for (int i = 0; i < userIdAdapter.getCount(); i++) {
                        String item = userIdAdapter.getItem(i);
                        if (item.contains("(")) {
                            String uid = item.substring(item.indexOf('(') + 1, item.indexOf(')'));
                            if (uid.equalsIgnoreCase(recipientId)) {
                                recipientIdSpinner.setSelection(i);
                                break;
                            }
                        } else if (item.equalsIgnoreCase(recipientId)) {
                            recipientIdSpinner.setSelection(i);
                            break;
                        }
                    }
                    recipientIdSpinner.setVisibility(View.VISIBLE);
                }
            }
        }

        // Date and time pickers
        Calendar expiryCalendar = Calendar.getInstance();
        if (notification != null && notification.getExpiresAt() != null) {
            expiryCalendar.setTime(notification.getExpiresAt().toDate());
        } else {
            expiryCalendar.add(Calendar.DAY_OF_MONTH, 7); // Default to 1 week from now
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        expiryDateButton.setText("Expiry Date: " + dateFormat.format(expiryCalendar.getTime()));
        expiryTimeButton.setText("Expiry Time: " + timeFormat.format(expiryCalendar.getTime()));

        expiryDateButton.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        expiryCalendar.set(Calendar.YEAR, year);
                        expiryCalendar.set(Calendar.MONTH, month);
                        expiryCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        expiryDateButton.setText("Expiry Date: " + dateFormat.format(expiryCalendar.getTime()));
                    },
                    expiryCalendar.get(Calendar.YEAR),
                    expiryCalendar.get(Calendar.MONTH),
                    expiryCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        expiryTimeButton.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    (view, hourOfDay, minute) -> {
                        expiryCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        expiryCalendar.set(Calendar.MINUTE, minute);
                        expiryTimeButton.setText("Expiry Time: " + timeFormat.format(expiryCalendar.getTime()));
                    },
                    expiryCalendar.get(Calendar.HOUR_OF_DAY),
                    expiryCalendar.get(Calendar.MINUTE),
                    true);
            timePickerDialog.show();
        });

        saveButton.setOnClickListener(v -> {
            String title = titleEdit.getText().toString().trim();
            String message = messageEdit.getText().toString().trim();
            
            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(message)) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String type = typeSpinner.getSelectedItem().toString().toLowerCase().replace(" ", "_");
            boolean isActive = activeSwitch.isChecked();
            String recipientType = recipientTypeSpinner.getSelectedItem().toString();
            String recipientTypeValue = "all";
            String recipientIdValue = null;
            if (recipientType.equals("All Users")) {
                recipientTypeValue = "all";
                recipientIdValue = null;
            } else if (recipientType.equals("User Group")) {
                recipientTypeValue = "group";
                recipientIdValue = recipientIdSpinner.getSelectedItem().toString();
            } else if (recipientType.equals("Specific User")) {
                recipientTypeValue = "user";
                String selected = recipientIdSpinner.getSelectedItem().toString();
                // Extract userId from "email (userId)" format
                if (selected.contains("(")) {
                    recipientIdValue = selected.substring(selected.indexOf('(') + 1, selected.indexOf(')'));
                } else {
                    recipientIdValue = selected;
                }
            }

            if (notification == null) {
                // Create new notification
                Notification newNotification = new Notification();
                newNotification.setId(UUID.randomUUID().toString());
                newNotification.setTitle(title);
                newNotification.setMessage(message);
                newNotification.setType(type);
                newNotification.setActive(isActive);
                newNotification.setCreatedAt(Timestamp.now());
                newNotification.setExpiresAt(new Timestamp(expiryCalendar.getTime()));
                newNotification.setRecipientType(recipientTypeValue);
                newNotification.setRecipientId(recipientIdValue);

                saveNotification(newNotification);
            } else {
                // Update existing notification
                notification.setTitle(title);
                notification.setMessage(message);
                notification.setType(type);
                notification.setActive(isActive);
                notification.setExpiresAt(new Timestamp(expiryCalendar.getTime()));
                notification.setRecipientType(recipientTypeValue);
                notification.setRecipientId(recipientIdValue);

                updateNotification(notification);
            }

            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void saveNotification(Notification notification) {
        db.collection("notifications").document(notification.getId())
                .set(notification)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Notification created successfully", Toast.LENGTH_SHORT).show();
                    fetchNotifications();
                    
                    // Send notification to users if active
                    if (notification.isActive()) {
                        sendNotificationToUsers(notification);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to create notification: " + e.getMessage(), 
                                 Toast.LENGTH_SHORT).show();
                });
    }

    private void updateNotification(Notification notification) {
        db.collection("notifications").document(notification.getId())
                .set(notification)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Notification updated successfully", Toast.LENGTH_SHORT).show();
                    fetchNotifications();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update notification: " + e.getMessage(), 
                                 Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteNotification(Notification notification) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Notification")
                .setMessage("Are you sure you want to delete this notification?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    db.collection("notifications").document(notification.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Notification deleted successfully", 
                                             Toast.LENGTH_SHORT).show();
                                fetchNotifications();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Failed to delete notification: " + e.getMessage(), 
                                             Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void toggleNotificationActive(Notification notification) {
        notification.setActive(!notification.isActive());
        updateNotification(notification);
    }

    private void sendNotificationToUsers(Notification notification) {
        // Send push notification to all users with type-specific priority
        NotificationUtil.sendNotification(this, notification.getTitle(), notification.getMessage(), notification.getType());
        
        // In a real app, you would use Firebase Cloud Messaging to send to specific users
        // For now, we'll just show a local notification
        Toast.makeText(this, "Notification sent to users", Toast.LENGTH_SHORT).show();
    }
} 