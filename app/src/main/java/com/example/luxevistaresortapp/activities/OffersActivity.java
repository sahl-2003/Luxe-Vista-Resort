package com.example.luxevistaresortapp.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.example.luxevistaresortapp.R;
import com.example.luxevistaresortapp.adapters.OfferAdapter;
import com.example.luxevistaresortapp.models.Offer;
import java.util.ArrayList;
import java.util.List;

public class OffersActivity extends AppCompatActivity {
    private RecyclerView offersRecyclerView;
    private OfferAdapter offerAdapter;
    private List<Offer> offerList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

        db = FirebaseFirestore.getInstance();
        offersRecyclerView = findViewById(R.id.offersRecyclerView);

        offerList = new ArrayList<>();
        offerAdapter = new OfferAdapter(this, offerList, null);

        offersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        offersRecyclerView.setAdapter(offerAdapter);

        fetchOffers();
    }

    private void fetchOffers() {
        db.collection("offers").whereEqualTo("active", true).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        offerList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Offer offer = document.toObject(Offer.class);
                            offerList.add(offer);
                        }
                        offerAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Failed to load offers", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}