package com.example.luxevistaresortapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.example.luxevistaresortapp.R;
import com.example.luxevistaresortapp.adapters.ServiceAdapter;
import com.example.luxevistaresortapp.models.Service;
import com.example.luxevistaresortapp.utils.FirestoreInitializer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceReservationActivity extends AppCompatActivity {
    private RecyclerView servicesRecyclerView;
    private Spinner serviceTypeSpinner;
    private CalendarView calendarView;
    private Button filterButton;
    private ServiceAdapter serviceAdapter;
    private List<Service> serviceList;
    private FirebaseFirestore db;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_reservation);

        db = FirebaseFirestore.getInstance();
        servicesRecyclerView = findViewById(R.id.servicesRecyclerView);
        serviceTypeSpinner = findViewById(R.id.serviceTypeSpinner);
        calendarView = findViewById(R.id.calendarView);
        filterButton = findViewById(R.id.filterButton);

        serviceList = new ArrayList<>();
        serviceAdapter = new ServiceAdapter(serviceList, service -> {
            if (selectedDate == null) {
                Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
                return;
            }
            reserveService(service);
        }, null);

        servicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        servicesRecyclerView.setAdapter(serviceAdapter);

        // Setup service type spinner dynamically from Firestore
        List<String> serviceTypeList = new ArrayList<>();
        ArrayAdapter<String> serviceTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, serviceTypeList);
        serviceTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serviceTypeSpinner.setAdapter(serviceTypeAdapter);
        db.collection("services").get().addOnSuccessListener(queryDocumentSnapshots -> {
            serviceTypeList.clear();
            serviceTypeList.add("All");
            for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                String type = doc.getString("type");
                if (type != null && !serviceTypeList.contains(type)) {
                    serviceTypeList.add(type);
                }
            }
            serviceTypeAdapter.notifyDataSetChanged();
        });

        // Setup calendar
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth);
            Log.d("ServiceReservation", "Selected date: " + selectedDate);
        });

        // Set default date to today
        Calendar calendar = Calendar.getInstance();
        selectedDate = String.format("%d-%02d-%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));
        Log.d("ServiceReservation", "Default date set to: " + selectedDate);

        // Initialize Firestore data
        FirestoreInitializer initializer = new FirestoreInitializer();
        initializer.addInitialServices();

        // Fetch services from Firestore
        fetchServices();

        filterButton.setOnClickListener(v -> {
            String selectedType = serviceTypeSpinner.getSelectedItem().toString();
            filterServices(selectedType);
        });

        Button showUnavailableDatesButton = findViewById(R.id.showUnavailableDatesButton);
        showUnavailableDatesButton.setOnClickListener(v -> {
            // Show unavailable dates for the first visible service in the list
            if (!serviceList.isEmpty()) {
                Service selectedService = serviceList.get(0);
                showUnavailableDatesDialog(selectedService);
            } else {
                Toast.makeText(this, "No service selected or available.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchServices() {
        db.collection("services").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        serviceList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Service service = document.toObject(Service.class);
                            Log.d("ServiceReservation", "Fetched service: " + service.getName());
                            serviceList.add(service);
                        }
                        Log.d("ServiceReservation", "Total services fetched: " + serviceList.size());
                        serviceAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Failed to load services: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("ServiceReservation", "Error fetching services: " + task.getException().getMessage());
                    }
                });
    }

    private void filterServices(String type) {
        List<Service> filteredList = new ArrayList<>();
        for (Service service : serviceList) {
            if (type.equals("All") || service.getType().equals(type)) {
                filteredList.add(service);
            }
        }
        Log.d("ServiceReservation", "Filtered services count: " + filteredList.size());
        serviceAdapter.updateList(filteredList);
    }

    private void reserveService(Service service) {
        String userId = com.example.luxevistaresortapp.utils.FirebaseHelper.getInstance().getCurrentUserId();
        if (userId == null) {
            Toast.makeText(this, "Please log in to reserve a service", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prevent overbooking
        if (service.getAvailableSlots() <= 0) {
            Toast.makeText(this, "No available slots for this service.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check for existing reservation for this service and date
        db.collection("reservations")
            .whereEqualTo("serviceId", service.getId())
            .whereEqualTo("date", selectedDate)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                if (!querySnapshot.isEmpty()) {
                    Toast.makeText(this, "Service not available on this date. Please choose another date.", Toast.LENGTH_SHORT).show();
                } else {
                    // No reservation exists, proceed to reserve
                    Map<String, Object> reservation = new HashMap<>();
                    reservation.put("userId", userId);
                    reservation.put("serviceId", service.getId());
                    reservation.put("serviceName", service.getName());
                    reservation.put("date", selectedDate);
                    reservation.put("timestamp", System.currentTimeMillis());
                    db.collection("reservations").document()
                        .set(reservation)
                        .addOnSuccessListener(aVoid -> {
                            // Decrease availableSlots by 1
                            db.collection("services").document(service.getId())
                                .update("availableSlots", service.getAvailableSlots() - 1);
                            Toast.makeText(this, "Successfully reserved " + service.getName() + " for " + selectedDate, Toast.LENGTH_SHORT).show();
                            Log.d("ServiceReservation", "Reservation saved for " + service.getName());
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to reserve service: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("ServiceReservation", "Error saving reservation: " + e.getMessage());
                        });
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to check availability: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    private void showUnavailableDatesDialog(Service service) {
        db.collection("reservations")
            .whereEqualTo("serviceId", service.getId())
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<String> unavailableDates = new ArrayList<>();
                for (var doc : querySnapshot) {
                    String date = doc.getString("date");
                    if (date != null) {
                        unavailableDates.add(date);
                    }
                }
                if (unavailableDates.isEmpty()) {
                    Toast.makeText(this, "All dates are available for this service.", Toast.LENGTH_SHORT).show();
                } else {
                    new android.app.AlertDialog.Builder(this)
                        .setTitle("Unavailable Dates")
                        .setMessage("These dates are already booked:\n" + android.text.TextUtils.join(", ", unavailableDates))
                        .setPositiveButton("OK", null)
                        .show();
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to fetch unavailable dates: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }
}