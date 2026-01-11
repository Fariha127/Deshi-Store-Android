package com.fariha.deshistore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyProfileActivity extends AppCompatActivity {

    private TextView tvFullName, tvEmail, tvPhoneNumber, tvDateOfBirth, tvGender, tvCity;
    private Button btnEditProfile, btnBackToHome, btnLogout;
    
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        
        userId = currentUser.getUid();
        
        initializeViews();
        loadUserData();
        setupClickListeners();
    }

    private void initializeViews() {
        tvFullName = findViewById(R.id.tvFullName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        tvDateOfBirth = findViewById(R.id.tvDateOfBirth);
        tvGender = findViewById(R.id.tvGender);
        tvCity = findViewById(R.id.tvCity);
        
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnBackToHome = findViewById(R.id.btnBackToHome);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void loadUserData() {
        mFirestore.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String fullName = documentSnapshot.getString("fullName");
                        String email = documentSnapshot.getString("email");
                        String phoneNumber = documentSnapshot.getString("phoneNumber");
                        String dateOfBirth = documentSnapshot.getString("dateOfBirth");
                        String gender = documentSnapshot.getString("gender");
                        String city = documentSnapshot.getString("city");
                        
                        tvFullName.setText(fullName != null ? fullName : "N/A");
                        tvEmail.setText(email != null ? email : "N/A");
                        tvPhoneNumber.setText(phoneNumber != null ? phoneNumber : "N/A");
                        tvDateOfBirth.setText(dateOfBirth != null ? dateOfBirth : "N/A");
                        tvGender.setText(gender != null ? gender : "N/A");
                        tvCity.setText(city != null ? city : "N/A");
                    } else {
                        Toast.makeText(MyProfileActivity.this, "User profile not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MyProfileActivity.this, "Failed to load user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setupClickListeners() {
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MyProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });
        
        btnBackToHome.setOnClickListener(v -> {
            startActivity(new Intent(MyProfileActivity.this, HomeActivity.class));
            finish();
        });
        
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(MyProfileActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MyProfileActivity.this, HomeActivity.class));
            finish();
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadUserData(); // Reload data when returning from edit screen
    }
}
