package com.example.luxevistaresortapp.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.luxevistaresortapp.R;
import com.example.luxevistaresortapp.adapters.AttractionAdapter;
import com.example.luxevistaresortapp.models.Attraction;
import com.google.firebase.firestore.*;
import java.util.*;

public class ManageAttractionsActivity extends AppCompatActivity {
    private RecyclerView attractionsRecyclerView;
    private AttractionAdapter adapter;
    private List<Attraction> attractionList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_attractions);

        db = FirebaseFirestore.getInstance();
        attractionsRecyclerView = findViewById(R.id.attractionsRecyclerView);
        attractionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AttractionAdapter(this, attractionList, new AttractionAdapter.OnAttractionActionListener() {
            @Override
            public void onEdit(Attraction attraction) {
                showAttractionDialog(attraction);
            }
            @Override
            public void onDelete(Attraction attraction) {
                deleteAttraction(attraction);
            }
        });
        attractionsRecyclerView.setAdapter(adapter);

        findViewById(R.id.addAttractionButton).setOnClickListener(v -> showAttractionDialog(null));
        fetchAttractions();
    }

    private void fetchAttractions() {
        db.collection("attractions").addSnapshotListener((snap, e) -> {
            if (e != null) return;
            attractionList.clear();
            for (DocumentSnapshot doc : snap.getDocuments()) {
                Attraction attraction = doc.toObject(Attraction.class);
                if (attraction != null) attractionList.add(attraction);
            }
            adapter.updateList(attractionList);
        });
    }

    private void showAttractionDialog(Attraction attractionToEdit) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_edit_attraction);
        EditText name = dialog.findViewById(R.id.attractionNameEditText);
        EditText desc = dialog.findViewById(R.id.attractionDescriptionEditText);
        EditText imageUrl = dialog.findViewById(R.id.attractionImageUrlEditText);

        if (attractionToEdit != null) {
            name.setText(attractionToEdit.getName());
            desc.setText(attractionToEdit.getDescription());
            imageUrl.setText(attractionToEdit.getImageUrl());
        }

        dialog.findViewById(R.id.saveAttractionButton).setOnClickListener(v -> {
            String n = name.getText().toString().trim();
            String d = desc.getText().toString().trim();
            String img = imageUrl.getText().toString().trim();

            if (TextUtils.isEmpty(n) || TextUtils.isEmpty(d) || TextUtils.isEmpty(img)) {
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
                return;
            }

            String id = (attractionToEdit != null) ? attractionToEdit.getId() : db.collection("attractions").document().getId();
            Attraction attraction = new Attraction(id, n, d, img);

            db.collection("attractions").document(id).set(attraction)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Attraction saved", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        dialog.show();
    }

    private void deleteAttraction(Attraction attraction) {
        new AlertDialog.Builder(this)
            .setTitle("Delete Attraction")
            .setMessage("Are you sure you want to delete this attraction?")
            .setPositiveButton("Delete", (d, w) -> {
                db.collection("attractions").document(attraction.getId()).delete()
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Attraction deleted", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
} 