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

public class CompanyVendorSignUpActivity extends AppCompatActivity {

    private EditText etFullName, etDesignation, etCompanyName, etCompanyEmail;
    private EditText etPassword, etConfirmPassword, etPhoneNumber;
    private EditText etRegistrationNumber, etBSTINumber, etAddress, etTINNumber;
    private Button btnRegister, btnBackToLogin;
    
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_vendor_signup);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        etFullName = findViewById(R.id.etFullName);
        etDesignation = findViewById(R.id.etDesignation);
        etCompanyName = findViewById(R.id.etCompanyName);
        etCompanyEmail = findViewById(R.id.etCompanyEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etRegistrationNumber = findViewById(R.id.etRegistrationNumber);
        etBSTINumber = findViewById(R.id.etBSTINumber);
        etAddress = findViewById(R.id.etAddress);
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
        String fullName = etFullName.getText().toString().trim();
        String designation = etDesignation.getText().toString().trim();
        String companyName = etCompanyName.getText().toString().trim();
        String companyEmail = etCompanyEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String registrationNumber = etRegistrationNumber.getText().toString().trim();
        String bstiNumber = etBSTINumber.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String tinNumber = etTINNumber.getText().toString().trim();

        // Validation
        if (fullName.isEmpty() || designation.isEmpty() || companyName.isEmpty() || 
            companyEmail.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || 
            phoneNumber.isEmpty() || address.isEmpty()) {
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
        mAuth.createUserWithEmailAndPassword(companyEmail, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveCompanyVendorData(user.getUid(), fullName, designation, companyName,
                                    companyEmail, phoneNumber, registrationNumber, bstiNumber, 
                                    address, tinNumber);
                        }
                    } else {
                        Toast.makeText(this, "Registration failed: " + 
                                task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveCompanyVendorData(String userId, String fullName, String designation,
                                       String companyName, String companyEmail, String phoneNumber,
                                       String registrationNumber, String bstiNumber, 
                                       String address, String tinNumber) {
        Map<String, Object> vendorData = new HashMap<>();
        vendorData.put("fullName", fullName);
        vendorData.put("designation", designation);
        vendorData.put("companyName", companyName);
        vendorData.put("companyEmail", companyEmail);
        vendorData.put("phoneNumber", phoneNumber);
        vendorData.put("registrationNumber", registrationNumber);
        vendorData.put("bstiNumber", bstiNumber);
        vendorData.put("address", address);
        vendorData.put("tinNumber", tinNumber);
        vendorData.put("userType", "Company Vendor");

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
