package com.example.luxevistaresortapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.luxevistaresortapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class
WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(user.getUid()).get().addOnSuccessListener(document -> {
                if (document.exists()) {
                    String role = document.getString("role");
                    if ("admin".equals(role)) {
                        startActivity(new Intent(WelcomeActivity.this, AdminDashboardActivity.class));
                    } else {
                        startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
                    }
                    finish();
                } else {
                    // If user data not found, sign out and show login
                    FirebaseAuth.getInstance().signOut();
                }
            }).addOnFailureListener(e -> {
                FirebaseAuth.getInstance().signOut();
            });
        }

        Button getStartedButton = findViewById(R.id.getStartedButton);
        getStartedButton.setOnClickListener(v -> {
            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
            finish();
        });
    }
} 