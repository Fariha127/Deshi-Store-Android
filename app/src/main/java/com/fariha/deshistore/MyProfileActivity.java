package com.fariha.deshistore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyProfileActivity extends AppCompatActivity {

    private TextView tvFullName, tvEmail, tvPhoneNumber, tvDateOfBirth, tvGender, tvCity;
    private Button btnEditProfile, btnBackToHome, btnLogout;
    
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        
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
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String fullName = dataSnapshot.child("fullName").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String phoneNumber = dataSnapshot.child("phoneNumber").getValue(String.class);
                    String dateOfBirth = dataSnapshot.child("dateOfBirth").getValue(String.class);
                    String gender = dataSnapshot.child("gender").getValue(String.class);
                    String city = dataSnapshot.child("city").getValue(String.class);
                    
                    tvFullName.setText(fullName != null ? fullName : "N/A");
                    tvEmail.setText(email != null ? email : "N/A");
                    tvPhoneNumber.setText(phoneNumber != null ? phoneNumber : "N/A");
                    tvDateOfBirth.setText(dateOfBirth != null ? dateOfBirth : "N/A");
                    tvGender.setText(gender != null ? gender : "N/A");
                    tvCity.setText(city != null ? city : "N/A");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MyProfileActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
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
