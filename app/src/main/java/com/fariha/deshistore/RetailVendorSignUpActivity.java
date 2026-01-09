package com.fariha.deshistore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RetailVendorSignUpActivity extends AppCompatActivity {

    private EditText etOwnerName, etShopName, etEmail, etPassword, etConfirmPassword;
    private EditText etPhoneNumber, etRegistrationNumber, etTradeLicense;
    private EditText etShopAddress, etTINNumber;
    private Button btnRegister, btnBackToLogin;
    
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retail_vendor_signup);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        etOwnerName = findViewById(R.id.etOwnerName);
        etShopName = findViewById(R.id.etShopName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etRegistrationNumber = findViewById(R.id.etRegistrationNumber);
        etTradeLicense = findViewById(R.id.etTradeLicense);
        etShopAddress = findViewById(R.id.etShopAddress);
        etTINNumber = findViewById(R.id.etTINNumber);
        btnRegister = findViewById(R.id.btnRegister);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);
    }

    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> handleRegistration());
        
        btnBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void handleRegistration() {
        String ownerName = etOwnerName.getText().toString().trim();
        String shopName = etShopName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String registrationNumber = etRegistrationNumber.getText().toString().trim();
        String tradeLicense = etTradeLicense.getText().toString().trim();
        String shopAddress = etShopAddress.getText().toString().trim();
        String tinNumber = etTINNumber.getText().toString().trim();

        // Validation
        if (ownerName.isEmpty() || shopName.isEmpty() || email.isEmpty() || 
            password.isEmpty() || confirmPassword.isEmpty() || phoneNumber.isEmpty() || 
            shopAddress.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create user in Firebase
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveRetailVendorData(user.getUid(), ownerName, shopName, email,
                                    phoneNumber, registrationNumber, tradeLicense, 
                                    shopAddress, tinNumber);
                        }
                    } else {
                        Toast.makeText(this, "Registration failed: " + 
                                task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveRetailVendorData(String userId, String ownerName, String shopName,
                                      String email, String phoneNumber, String registrationNumber,
                                      String tradeLicense, String shopAddress, String tinNumber) {
        Map<String, Object> vendorData = new HashMap<>();
        vendorData.put("ownerName", ownerName);
        vendorData.put("shopName", shopName);
        vendorData.put("email", email);
        vendorData.put("phoneNumber", phoneNumber);
        vendorData.put("registrationNumber", registrationNumber);
        vendorData.put("tradeLicense", tradeLicense);
        vendorData.put("shopAddress", shopAddress);
        vendorData.put("tinNumber", tinNumber);
        vendorData.put("userType", "Retail Vendor");

        mDatabase.child("users").child(userId).setValue(vendorData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save vendor data", Toast.LENGTH_SHORT).show();
                });
    }
}
