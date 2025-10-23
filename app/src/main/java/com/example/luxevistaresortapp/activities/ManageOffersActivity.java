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
import com.example.luxevistaresortapp.adapters.OfferAdapter;
import com.example.luxevistaresortapp.models.Offer;
import com.google.firebase.firestore.*;
import java.util.*;

public class ManageOffersActivity extends AppCompatActivity {
    private RecyclerView offersRecyclerView;
    private OfferAdapter adapter;
    private List<Offer> offerList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_offers);

        db = FirebaseFirestore.getInstance();
        offersRecyclerView = findViewById(R.id.offersRecyclerView);
        offersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OfferAdapter(this, offerList, new OfferAdapter.OnOfferActionListener() {
            @Override
            public void onEdit(Offer offer) {
                showOfferDialog(offer);
            }
            @Override
            public void onDelete(Offer offer) {
                deleteOffer(offer);
            }
        });
        offersRecyclerView.setAdapter(adapter);

        findViewById(R.id.addOfferButton).setOnClickListener(v -> showOfferDialog(null));
        fetchOffers();
    }

    private void fetchOffers() {
        db.collection("offers").addSnapshotListener((snap, e) -> {
            if (e != null) return;
            offerList.clear();
            for (DocumentSnapshot doc : snap.getDocuments()) {
                Offer offer = doc.toObject(Offer.class);
                if (offer != null) offerList.add(offer);
            }
            adapter.updateList(offerList);
        });
    }

    private void showOfferDialog(Offer offerToEdit) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_edit_offer);
        EditText title = dialog.findViewById(R.id.offerTitleEditText);
        EditText desc = dialog.findViewById(R.id.offerDescriptionEditText);

        if (offerToEdit != null) {
            title.setText(offerToEdit.getTitle());
            desc.setText(offerToEdit.getDescription());
        }

        dialog.findViewById(R.id.saveOfferButton).setOnClickListener(v -> {
            String t = title.getText().toString().trim();
            String d = desc.getText().toString().trim();

            if (TextUtils.isEmpty(t) || TextUtils.isEmpty(d)) {
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
                return;
            }

            String id = (offerToEdit != null) ? offerToEdit.getId() : db.collection("offers").document().getId();
            Offer offer = new Offer(id, t, d, true); // Default to active

            db.collection("offers").document(id).set(offer)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Offer saved", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        dialog.show();
    }

    private void deleteOffer(Offer offer) {
        new AlertDialog.Builder(this)
            .setTitle("Delete Offer")
            .setMessage("Are you sure you want to delete this offer?")
            .setPositiveButton("Delete", (d, w) -> {
                db.collection("offers").document(offer.getId()).delete()
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Offer deleted", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
} 