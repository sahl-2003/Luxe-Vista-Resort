package com.example.luxevistaresortapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.luxevistaresortapp.R;
import com.example.luxevistaresortapp.utils.NotificationService;
import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboardActivity extends AppCompatActivity {
    private Button logoutButton;
    private Button manageRoomsButton;
    private Button manageServicesButton;
    private Button manageOffersButton;
    private Button manageAttractionsButton;
    private Button manageNotificationsButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        mAuth = FirebaseAuth.getInstance();
        logoutButton = findViewById(R.id.logoutButton);
        manageRoomsButton = findViewById(R.id.manageRoomsButton);
        manageServicesButton = findViewById(R.id.manageServicesButton);
        manageOffersButton = findViewById(R.id.manageOffersButton);
        manageAttractionsButton = findViewById(R.id.manageAttractionsButton);
        manageNotificationsButton = findViewById(R.id.manageNotificationsButton);

        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(AdminDashboardActivity.this, LoginActivity.class));
            finish();
        });

        manageRoomsButton.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, ManageRoomsActivity.class));
        });

        manageServicesButton.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, ManageServicesActivity.class));
        });

        manageOffersButton.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, ManageOffersActivity.class));
        });

        manageAttractionsButton.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, ManageAttractionsActivity.class));
        });

        manageNotificationsButton.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, ManageNotificationsActivity.class));
        });

        // TODO: Add admin features (manage services, users, etc.)
    }
} 