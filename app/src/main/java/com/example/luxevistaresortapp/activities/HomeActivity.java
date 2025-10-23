package com.example.luxevistaresortapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.example.luxevistaresortapp.R;
import com.example.luxevistaresortapp.adapters.AttractionAdapter;
import com.example.luxevistaresortapp.models.Attraction;
import com.example.luxevistaresortapp.utils.NotificationUtil;


public class HomeActivity extends AppCompatActivity {
    private Button roomBookingButton, serviceReservationButton, attractionsButton, profileButton, logoutButton, notificationsButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();

        roomBookingButton = findViewById(R.id.roomBookingButton);
        serviceReservationButton = findViewById(R.id.serviceReservationButton);
        attractionsButton = findViewById(R.id.attractionsButton);
        profileButton = findViewById(R.id.profileButton);
        logoutButton = findViewById(R.id.logoutButton);
        notificationsButton = findViewById(R.id.notificationsButton);

        roomBookingButton.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, RoomBookingActivity.class));
        });

        serviceReservationButton.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ServiceReservationActivity.class));
        });

        attractionsButton.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, AttractionsActivity.class));
        });

        profileButton.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        });

        notificationsButton.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, NotificationsActivity.class));
        });

        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        });
    }
}