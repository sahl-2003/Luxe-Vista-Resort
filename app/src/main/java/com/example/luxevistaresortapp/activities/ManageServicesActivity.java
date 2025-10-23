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
import com.example.luxevistaresortapp.adapters.ServiceAdapter;
import com.example.luxevistaresortapp.models.Service;
import com.google.firebase.firestore.*;
import java.util.*;

public class ManageServicesActivity extends AppCompatActivity {
    private RecyclerView servicesRecyclerView;
    private ServiceAdapter adapter;
    private List<Service> serviceList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_services);

        db = FirebaseFirestore.getInstance();
        servicesRecyclerView = findViewById(R.id.servicesRecyclerView);
        servicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ServiceAdapter(serviceList, service -> {}, new ServiceAdapter.OnServiceActionListener() {
            @Override
            public void onEdit(Service service) {
                showServiceDialog(service);
            }
            @Override
            public void onDelete(Service service) {
                deleteService(service);
            }
        });
        servicesRecyclerView.setAdapter(adapter);

        findViewById(R.id.addServiceButton).setOnClickListener(v -> showServiceDialog(null));
        fetchServices();
    }

    private void fetchServices() {
        db.collection("services").addSnapshotListener((snap, e) -> {
            if (e != null) return;
            serviceList.clear();
            for (DocumentSnapshot doc : snap.getDocuments()) {
                Service service = doc.toObject(Service.class);
                if (service != null) serviceList.add(service);
            }
            adapter.updateList(serviceList);
        });
    }

    private void showServiceDialog(Service serviceToEdit) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_edit_service);
        EditText name = dialog.findViewById(R.id.serviceNameEditText);
        EditText type = dialog.findViewById(R.id.serviceTypeEditText);
        EditText price = dialog.findViewById(R.id.servicePriceEditText);
        EditText imageUrl = dialog.findViewById(R.id.serviceImageUrlEditText);
        EditText availableSlots = dialog.findViewById(R.id.serviceAvailableSlotsEditText);

        if (serviceToEdit != null) {
            name.setText(serviceToEdit.getName());
            type.setText(serviceToEdit.getType());
            price.setText(String.valueOf(serviceToEdit.getPrice()));
            imageUrl.setText(serviceToEdit.getImageUrl());
            availableSlots.setText(String.valueOf(serviceToEdit.getAvailableSlots()));
        }

        dialog.findViewById(R.id.saveServiceButton).setOnClickListener(v -> {
            String n = name.getText().toString().trim();
            String t = type.getText().toString().trim();
            String p = price.getText().toString().trim();
            String img = imageUrl.getText().toString().trim();
            String slotsStr = availableSlots.getText().toString().trim();

            if (TextUtils.isEmpty(n) || TextUtils.isEmpty(t) || TextUtils.isEmpty(p) || TextUtils.isEmpty(img) || TextUtils.isEmpty(slotsStr)) {
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
                return;
            }

            double priceVal = Double.parseDouble(p);
            int slotsVal = Integer.parseInt(slotsStr);
            String id = (serviceToEdit != null) ? serviceToEdit.getId() : db.collection("services").document().getId();
            Service service = new Service(id, n, t, priceVal, img, slotsVal);

            db.collection("services").document(id).set(service)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Service saved", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        dialog.show();
    }

    private void deleteService(Service service) {
        new AlertDialog.Builder(this)
            .setTitle("Delete Service")
            .setMessage("Are you sure you want to delete this service?")
            .setPositiveButton("Delete", (d, w) -> {
                db.collection("services").document(service.getId()).delete()
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Service deleted", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
} 