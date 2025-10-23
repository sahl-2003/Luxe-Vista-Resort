package com.example.luxevistaresortapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.example.luxevistaresortapp.R;
import com.example.luxevistaresortapp.adapters.RoomAdapter;
import com.example.luxevistaresortapp.models.Room;
import com.example.luxevistaresortapp.utils.FirestoreInitializer;
import java.util.ArrayList;
import java.util.List;

public class RoomBookingActivity extends AppCompatActivity {
    private RecyclerView roomsRecyclerView;
    private Spinner roomTypeSpinner, sortSpinner, availabilitySpinner;
    private Button filterButton, exploreAttractionsButton, viewOffersButton;
    private RoomAdapter roomAdapter;
    private List<Room> roomList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_booking);

        db = FirebaseFirestore.getInstance();
        roomsRecyclerView = findViewById(R.id.roomsRecyclerView);
        roomTypeSpinner = findViewById(R.id.roomTypeSpinner);
        sortSpinner = findViewById(R.id.sortSpinner);
        filterButton = findViewById(R.id.filterButton);
        exploreAttractionsButton = findViewById(R.id.exploreAttractionsButton);
        viewOffersButton = findViewById(R.id.viewOffersButton);
        availabilitySpinner = findViewById(R.id.availabilitySpinner);

        roomList = new ArrayList<>();
        roomAdapter = new RoomAdapter(this, roomList, null);

        roomsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        roomsRecyclerView.setAdapter(roomAdapter);

        // Setup room type spinner dynamically from Firestore
        List<String> roomTypeList = new ArrayList<>();
        ArrayAdapter<String> roomTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roomTypeList);
        roomTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomTypeSpinner.setAdapter(roomTypeAdapter);
        db.collection("rooms").get().addOnSuccessListener(queryDocumentSnapshots -> {
            roomTypeList.clear();
            roomTypeList.add("All");
            for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                String type = doc.getString("type");
                if (type != null && !roomTypeList.contains(type)) {
                    roomTypeList.add(type);
                }
            }
            roomTypeAdapter.notifyDataSetChanged();
        });

        // Setup sort spinner (static)
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(
                this, R.array.sort_options, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);

        // Setup availability spinner
        ArrayAdapter<CharSequence> availabilityAdapter = ArrayAdapter.createFromResource(
                this, R.array.availability_options, android.R.layout.simple_spinner_item);
        availabilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        availabilitySpinner.setAdapter(availabilityAdapter);

        // Initialize Firestore data
        FirestoreInitializer initializer = new FirestoreInitializer();
        initializer.addInitialRooms();
        initializer.addInitialAttractions();
        initializer.addInitialOffers();

        // Fetch rooms from Firestore
        fetchRooms();

        filterButton.setOnClickListener(v -> {
            String selectedType = roomTypeSpinner.getSelectedItem().toString();
            String sortOption = sortSpinner.getSelectedItem().toString();
            String availabilityOption = availabilitySpinner.getSelectedItem().toString();
            filterAndSortRooms(selectedType, sortOption, availabilityOption);
        });

        exploreAttractionsButton.setOnClickListener(v -> {
            Intent intent = new Intent(RoomBookingActivity.this, AttractionsActivity.class);
            startActivity(intent);
        });

        viewOffersButton.setOnClickListener(v -> {
            Intent intent = new Intent(RoomBookingActivity.this, OffersActivity.class);
            startActivity(intent);
        });
    }

    private void fetchRooms() {
        db.collection("rooms").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        roomList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Room room = document.toObject(Room.class);
                            Log.d("RoomBookingActivity", "Fetched room: " + room.getName() + ", Image URL: " + room.getImageUrl());
                            roomList.add(room);
                        }
                        Log.d("RoomBookingActivity", "Total rooms fetched: " + roomList.size());
                        roomAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Failed to load rooms: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("RoomBookingActivity", "Error fetching rooms: " + task.getException().getMessage());
                    }
                });
    }

    private void filterAndSortRooms(String type, String sortOption, String availabilityOption) {
        List<Room> filteredList = new ArrayList<>();
        for (Room room : roomList) {
            boolean typeMatch = type.equals("All") || room.getType().equals(type);
            boolean availabilityMatch = true;
            if (availabilityOption.equals("Available")) {
                availabilityMatch = room.isAvailable();
            } else if (availabilityOption.equals("Unavailable")) {
                availabilityMatch = !room.isAvailable();
            }
            if (typeMatch && availabilityMatch) {
                filteredList.add(room);
            }
        }

        if (sortOption.equals("Price: Low to High")) {
            filteredList.sort((r1, r2) -> Double.compare(r1.getPrice(), r2.getPrice()));
        } else if (sortOption.equals("Price: High to Low")) {
            filteredList.sort((r1, r2) -> Double.compare(r2.getPrice(), r1.getPrice()));
        }

        roomAdapter.updateList(filteredList);
    }
}