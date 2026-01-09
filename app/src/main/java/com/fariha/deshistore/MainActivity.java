package com.fariha.deshistore;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize Firebase and create manufacturer accounts (runs once)
        FirebaseInitializer firebaseInitializer = new FirebaseInitializer(this);
        firebaseInitializer.createManufacturerAccounts();
        
        // Navigate to HomeActivity
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}