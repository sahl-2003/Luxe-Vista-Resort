package com.example.luxevistaresortapp.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.luxevistaresortapp.R;
import com.example.luxevistaresortapp.utils.FirestoreInitializer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firestore data
        FirestoreInitializer initializer = new FirestoreInitializer();
        initializer.initializeAllData();

        // Proceed with the rest of your MainActivity logic
    }
}