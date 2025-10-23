package com.example.luxevistaresortapp.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.luxevistaresortapp.R;
import com.example.luxevistaresortapp.adapters.AttractionAdapter;
import com.example.luxevistaresortapp.models.Attraction;
import com.example.luxevistaresortapp.utils.NotificationUtil;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AttractionsActivity extends AppCompatActivity {

    private RecyclerView attractionsRecyclerView;
    private AttractionAdapter attractionAdapter;
    private List<Attraction> attractionList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attractions);

        db = FirebaseFirestore.getInstance();
        attractionsRecyclerView = findViewById(R.id.attractionsRecyclerView);

        attractionList = new ArrayList<>();
        attractionAdapter = new AttractionAdapter(this, attractionList, null);

        attractionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        attractionsRecyclerView.setAdapter(attractionAdapter);

        fetchAttractions();
        checkForSpecialOffers();
    }

    private void fetchAttractions() {
        db.collection("attractions").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        attractionList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Attraction attraction = document.toObject(Attraction.class);
                            attractionList.add(attraction);
                        }
                        attractionAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Failed to load attractions", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkForSpecialOffers() {
        db.collection("offers").whereEqualTo("active", true).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String offerTitle = document.getString("title");
                            String offerDescription = document.getString("description");
                            if (offerTitle != null && offerDescription != null) {
                                NotificationUtil.sendNotification(
                                        this,
                                        "Special Offer",
                                        offerTitle + ": " + offerDescription
                                );
                            }
                        }
                    }
                });
    }
}
