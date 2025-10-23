package com.example.luxevistaresortapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.luxevistaresortapp.R;
import com.example.luxevistaresortapp.adapters.BookingAdapter;
import com.example.luxevistaresortapp.adapters.ServiceHistoryAdapter;
import com.example.luxevistaresortapp.models.Booking;
import com.example.luxevistaresortapp.models.ServiceReservation;
import com.example.luxevistaresortapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.widget.LinearLayout;

public class ProfileActivity extends AppCompatActivity {
    private EditText nameEditText, preferencesEditText, travelDatesEditText;
    private TextView emailTextView;
    private RecyclerView bookingHistoryRecyclerView, serviceHistoryRecyclerView;
    private BookingAdapter bookingAdapter;
    private ServiceHistoryAdapter serviceHistoryAdapter;
    private Button saveButton, logoutButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        nameEditText = findViewById(R.id.nameEditText);
        emailTextView = findViewById(R.id.emailTextView);
        preferencesEditText = findViewById(R.id.preferencesEditText);
        travelDatesEditText = findViewById(R.id.travelDatesEditText);
        bookingHistoryRecyclerView = findViewById(R.id.bookingHistoryRecyclerView);
        serviceHistoryRecyclerView = findViewById(R.id.serviceHistoryRecyclerView);
        saveButton = findViewById(R.id.saveButton);
        logoutButton = findViewById(R.id.logoutButton);
        progressBar = findViewById(R.id.progressBar);

        // Setup RecyclerView
        bookingAdapter = new BookingAdapter();
        bookingHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookingHistoryRecyclerView.setAdapter(bookingAdapter);

        serviceHistoryAdapter = new ServiceHistoryAdapter(new ArrayList<>());
        serviceHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        serviceHistoryRecyclerView.setAdapter(serviceHistoryAdapter);

        // Fetch user profile and booking history
        fetchUserProfile();

        saveButton.setOnClickListener(v -> saveUserProfile());

        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        Button updatePasswordButton = findViewById(R.id.updatePasswordButton);
        updatePasswordButton.setOnClickListener(v -> showUpdatePasswordDialog());

        bookingAdapter.setOnBookingCancelListener(booking -> {
            new AlertDialog.Builder(this)
                .setTitle("Cancel Booking")
                .setMessage("Are you sure you want to cancel this booking?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    db.collection("bookings")
                        .whereEqualTo("bookingId", booking.getBookingId())
                        .get()
                        .addOnSuccessListener(querySnapshot -> {
                            for (var doc : querySnapshot) {
                                doc.getReference().delete();
                            }
                            // After deleting, check if there are any other bookings for this room
                            db.collection("bookings")
                                .whereEqualTo("roomId", booking.getRoomId())
                                .get()
                                .addOnSuccessListener(roomBookings -> {
                                    if (roomBookings.isEmpty()) {
                                        // No other bookings, set room as available
                                        db.collection("rooms").document(booking.getRoomId())
                                            .update("available", true);
                                    }
                                });
                            Toast.makeText(this, "Booking cancelled", Toast.LENGTH_SHORT).show();
                            fetchUserProfile();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to cancel booking: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                })
                .setNegativeButton("No", null)
                .show();
        });
        serviceHistoryAdapter.setOnServiceCancelListener(reservation -> {
            new AlertDialog.Builder(this)
                .setTitle("Cancel Service Reservation")
                .setMessage("Are you sure you want to cancel this service reservation?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    db.collection("reservations")
                        .whereEqualTo("userId", userId)
                        .whereEqualTo("serviceId", reservation.getServiceId())
                        .whereEqualTo("date", reservation.getDate())
                        .get()
                        .addOnSuccessListener(querySnapshot -> {
                            for (var doc : querySnapshot) {
                                doc.getReference().delete();
                            }
                            // Increase availableSlots by 1
                            db.collection("services").document(reservation.getServiceId())
                                .update("availableSlots", com.google.firebase.firestore.FieldValue.increment(1));
                            Toast.makeText(this, "Service reservation cancelled", Toast.LENGTH_SHORT).show();
                            fetchUserProfile();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to cancel reservation: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                })
                .setNegativeButton("No", null)
                .show();
        });
    }

    private void fetchUserProfile() {
        progressBar.setVisibility(View.VISIBLE);

        // Fetch user profile
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            nameEditText.setText(user.getName());
                            emailTextView.setText("Email: " + user.getEmail());
                            preferencesEditText.setText(user.getPreferences() != null ? user.getPreferences() : "");
                            travelDatesEditText.setText(user.getTravelDates() != null ? user.getTravelDates() : "");
                        }
                    } else {
                        Toast.makeText(this, "Profile not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                })
                .addOnCompleteListener(task -> progressBar.setVisibility(View.GONE));

        // Fetch booking history
        db.collection("bookings").whereEqualTo("userId", userId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Booking> bookings = new ArrayList<>();
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.d("ProfileActivity", "No bookings found for userId: " + userId);
                    } else {
                        for (var doc : queryDocumentSnapshots) {
                            Booking booking = doc.toObject(Booking.class);
                            bookings.add(booking);
                        }
                        Log.d("ProfileActivity", "Bookings loaded: " + bookings.size() + " items");
                    }
                    bookingAdapter.setBookingList(bookings);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load booking history: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("ProfileActivity", "Error loading bookings: " + e.getMessage());
                });

        // Fetch service reservation history
        db.collection("reservations").whereEqualTo("userId", userId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ServiceReservation> serviceHistory = new ArrayList<>();
                    for (var doc : queryDocumentSnapshots) {
                        ServiceReservation reservation = doc.toObject(ServiceReservation.class);
                        serviceHistory.add(reservation);
                    }
                    serviceHistoryAdapter.updateList(serviceHistory);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load service history: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("ProfileActivity", "Error loading service history: " + e.getMessage());
                });
    }

    private void saveUserProfile() {
        String name = nameEditText.getText().toString().trim();
        String preferences = preferencesEditText.getText().toString().trim();
        String travelDates = travelDatesEditText.getText().toString().trim();

        // Validate name
        if (name.isEmpty()) {
            nameEditText.setError("Name cannot be empty");
            nameEditText.requestFocus();
            return;
        }

        // Validate travel dates format (MM/DD/YYYY - MM/DD/YYYY)
        if (!travelDates.isEmpty() && !isValidTravelDates(travelDates)) {
            travelDatesEditText.setError("Invalid format. Use MM/DD/YYYY - MM/DD/YYYY");
            travelDatesEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        saveButton.setEnabled(false);

        User user = new User(userId, name, mAuth.getCurrentUser().getEmail(), preferences, travelDates);
        db.collection("users").document(userId).set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                })
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    saveButton.setEnabled(true);
                });
    }

    private boolean isValidTravelDates(String travelDates) {
        // Expected format: MM/DD/YYYY - MM/DD/YYYY
        String regex = "^(0[1-9]|1[0-2])/(0[1-9]|[12][0-9]|3[01])/[0-9]{4}\\s-\\s(0[1-9]|1[0-2])/(0[1-9]|[12][0-9]|3[01])/[0-9]{4}$";
        if (!travelDates.matches(regex)) {
            return false;
        }

        // Additional validation: ensure end date is after start date
        try {
            String[] dates = travelDates.split(" - ");
            String startDate = dates[0];
            String endDate = dates[1];

            String[] startParts = startDate.split("/");
            String[] endParts = endDate.split("/");

            int startMonth = Integer.parseInt(startParts[0]);
            int startDay = Integer.parseInt(startParts[1]);
            int startYear = Integer.parseInt(startParts[2]);

            int endMonth = Integer.parseInt(endParts[0]);
            int endDay = Integer.parseInt(endParts[1]);
            int endYear = Integer.parseInt(endParts[2]);

            // Simple date comparison
            if (startYear > endYear) {
                return false;
            } else if (startYear == endYear) {
                if (startMonth > endMonth) {
                    return false;
                } else if (startMonth == endMonth) {
                    return startDay <= endDay;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void showUpdatePasswordDialog() {
        // Create a layout for the dialog
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText newPasswordInput = new EditText(this);
        newPasswordInput.setHint("New password");
        newPasswordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

        final EditText confirmPasswordInput = new EditText(this);
        confirmPasswordInput.setHint("Confirm password");
        confirmPasswordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

        layout.addView(newPasswordInput);
        layout.addView(confirmPasswordInput);

        new AlertDialog.Builder(this)
            .setTitle("Update Password")
            .setView(layout)
            .setPositiveButton("Update", (dialog, which) -> {
                String newPassword = newPasswordInput.getText().toString().trim();
                String confirmPassword = confirmPasswordInput.getText().toString().trim();

                if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (newPassword.length() < 6) {
                    Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.getCurrentUser().updatePassword(newPassword)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to update password: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
}