package com.example.luxevistaresortapp.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.luxevistaresortapp.R;
import com.example.luxevistaresortapp.adapters.RoomAdapter;
import com.example.luxevistaresortapp.models.Room;
import com.google.firebase.firestore.*;
import java.util.*;

public class ManageRoomsActivity extends AppCompatActivity {
    private RecyclerView roomsRecyclerView;
    private RoomAdapter adapter;
    private List<Room> roomList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_rooms);

        db = FirebaseFirestore.getInstance();
        roomsRecyclerView = findViewById(R.id.roomsRecyclerView);
        roomsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RoomAdapter(this, roomList, new RoomAdapter.OnRoomActionListener() {
            @Override
            public void onEdit(Room room) {
                showRoomDialog(room);
            }
            @Override
            public void onDelete(Room room) {
                deleteRoom(room);
            }
        });
        roomsRecyclerView.setAdapter(adapter);

        findViewById(R.id.addRoomButton).setOnClickListener(v -> showRoomDialog(null));
        fetchRooms();
    }

    private void fetchRooms() {
        db.collection("rooms").addSnapshotListener((snap, e) -> {
            if (e != null) return;
            roomList.clear();
            for (DocumentSnapshot doc : snap.getDocuments()) {
                Room room = doc.toObject(Room.class);
                if (room != null) roomList.add(room);
            }
            adapter.updateList(roomList);
        });
    }

    private void showRoomDialog(Room roomToEdit) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_edit_room);
        EditText name = dialog.findViewById(R.id.roomNameEditText);
        EditText type = dialog.findViewById(R.id.roomTypeEditText);
        EditText price = dialog.findViewById(R.id.roomPriceEditText);
        EditText imageUrl = dialog.findViewById(R.id.roomImageUrlEditText);
        EditText desc = dialog.findViewById(R.id.roomDescriptionEditText);

        if (roomToEdit != null) {
            name.setText(roomToEdit.getName());
            type.setText(roomToEdit.getType());
            price.setText(String.valueOf(roomToEdit.getPrice()));
            imageUrl.setText(roomToEdit.getImageUrl());
            desc.setText(roomToEdit.getDescription());
        }

        dialog.findViewById(R.id.saveRoomButton).setOnClickListener(v -> {
            String n = name.getText().toString().trim();
            String t = type.getText().toString().trim();
            String p = price.getText().toString().trim();
            String img = imageUrl.getText().toString().trim();
            String d = desc.getText().toString().trim();

            if (TextUtils.isEmpty(n) || TextUtils.isEmpty(t) || TextUtils.isEmpty(p) || TextUtils.isEmpty(img)) {
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
                return;
            }

            double priceVal = Double.parseDouble(p);
            String id = (roomToEdit != null) ? roomToEdit.getId() : db.collection("rooms").document().getId();
            Room room = new Room(id, n, t, priceVal, img, d);

            db.collection("rooms").document(id).set(room)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Room saved", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        dialog.show();
    }

    private void deleteRoom(Room room) {
        new AlertDialog.Builder(this)
            .setTitle("Delete Room")
            .setMessage("Are you sure you want to delete this room?")
            .setPositiveButton("Delete", (d, w) -> {
                db.collection("rooms").document(room.getId()).delete()
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Room deleted", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
} 