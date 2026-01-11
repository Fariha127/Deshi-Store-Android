package com.fariha.deshistore;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPhoneNumber, etDateOfBirth, etCity;
    private EditText etCurrentPassword, etNewPassword, etConfirmNewPassword;
    private Spinner spinnerGender;
    private Button btnSendCode, btnSaveProfile, btnCancel, btnBackToHome, btnLogout;
    
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String userId;
    private String originalEmail;
    private boolean emailChanged = false;
    private boolean emailVerified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

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
        originalEmail = currentUser.getEmail();
        
        initializeViews();
        setupGenderSpinner();
        loadUserData();
        setupClickListeners();
    }

    private void initializeViews() {
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        etCity = findViewById(R.id.etCity);
        spinnerGender = findViewById(R.id.spinnerGender);
        
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword);
        
        btnSendCode = findViewById(R.id.btnSendCode);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnCancel = findViewById(R.id.btnCancel);
        btnBackToHome = findViewById(R.id.btnBackToHome);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void setupGenderSpinner() {
        String[] genders = {"Male", "Female", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, genders);
        spinnerGender.setAdapter(adapter);
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
                        
                        if (fullName != null && !fullName.isEmpty()) etFullName.setText(fullName);
                        if (email != null && !email.isEmpty()) etEmail.setText(email);
                        if (phoneNumber != null && !phoneNumber.isEmpty()) etPhoneNumber.setText(phoneNumber);
                        if (dateOfBirth != null && !dateOfBirth.isEmpty()) etDateOfBirth.setText(dateOfBirth);
                        if (city != null && !city.isEmpty()) etCity.setText(city);
                        
                        // Set gender spinner
                        if (gender != null && !gender.isEmpty()) {
                            String[] genders = {"Male", "Female", "Other"};
                            for (int i = 0; i < genders.length; i++) {
                                if (genders[i].equals(gender)) {
                                    spinnerGender.setSelection(i);
                                    break;
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProfileActivity.this, "Failed to load user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setupClickListeners() {
        // Date picker for date of birth
        etDateOfBirth.setOnClickListener(v -> showDatePicker());
        
        // Send Code button for email verification
        btnSendCode.setOnClickListener(v -> sendVerificationCode());
        
        // Save Profile button
        btnSaveProfile.setOnClickListener(v -> saveProfile());
        
        // Cancel button
        btnCancel.setOnClickListener(v -> {
            finish();
        });
        
        // Back to Home button
        btnBackToHome.setOnClickListener(v -> {
            startActivity(new Intent(EditProfileActivity.this, HomeActivity.class));
            finish();
        });
        
        // Logout button
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(EditProfileActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(EditProfileActivity.this, HomeActivity.class));
            finish();
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    etDateOfBirth.setText(date);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void sendVerificationCode() {
        String newEmail = etEmail.getText().toString().trim();
        
        if (TextUtils.isEmpty(newEmail)) {
            Toast.makeText(this, "Please enter email address", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (newEmail.equals(originalEmail)) {
            Toast.makeText(this, "This is your current email. No verification needed.", Toast.LENGTH_SHORT).show();
            emailVerified = true;
            return;
        }
        
        emailChanged = true;
        
        // Use the existing email verification system
        String verificationCode = EmailSender.generateVerificationCode();
        
        EmailSender.sendVerificationEmail(newEmail, verificationCode, new EmailSender.EmailCallback() {
            @Override
            public void onSending() {
                Toast.makeText(EditProfileActivity.this, "Sending verification code...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess() {
                // Launch email verification activity
                Intent intent = new Intent(EditProfileActivity.this, EmailVerificationActivity.class);
                intent.putExtra("email", newEmail);
                intent.putExtra("verification_code", verificationCode);
                intent.putExtra("from_edit_profile", true);
                startActivityForResult(intent, 200);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(EditProfileActivity.this, "Failed to send verification code: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK) {
            emailVerified = true;
            Toast.makeText(this, "Email verified successfully", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProfile() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String dateOfBirth = etDateOfBirth.getText().toString().trim();
        String gender = spinnerGender.getSelectedItem().toString();
        String city = etCity.getText().toString().trim();
        
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmNewPassword = etConfirmNewPassword.getText().toString().trim();

        // Validate required fields
        if (TextUtils.isEmpty(fullName)) {
            Toast.makeText(this, "Please enter full name", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if email changed and needs verification
        if (emailChanged && !email.equals(originalEmail) && !emailVerified) {
            Toast.makeText(this, "Please verify your new email address first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate password change if user wants to change password
        if (!TextUtils.isEmpty(currentPassword) || !TextUtils.isEmpty(newPassword) || !TextUtils.isEmpty(confirmNewPassword)) {
            if (TextUtils.isEmpty(currentPassword)) {
                Toast.makeText(this, "Please enter current password", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (TextUtils.isEmpty(newPassword)) {
                Toast.makeText(this, "Please enter new password", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (!newPassword.equals(confirmNewPassword)) {
                Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (newPassword.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Change password
            changePassword(currentPassword, newPassword);
        }

        // Update profile data in Firestore
        Map<String, Object> updates = new HashMap<>();
        updates.put("fullName", fullName);
        updates.put("email", email);
        updates.put("phoneNumber", phoneNumber);
        updates.put("dateOfBirth", dateOfBirth);
        updates.put("gender", gender);
        updates.put("city", city);

        mFirestore.collection("users").document(userId).update(updates)
                .addOnSuccessListener(aVoid -> {
                    // Update email in Firebase Auth if changed
                    if (emailChanged && emailVerified && !email.equals(originalEmail)) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            user.updateEmail(email)
                                    .addOnSuccessListener(aVoid1 -> {
                                        Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(EditProfileActivity.this, "Failed to update email in authentication: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProfileActivity.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void changePassword(String currentPassword, String newPassword) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null || user.getEmail() == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        // Re-authenticate user before changing password
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
        user.reauthenticate(credential)
                .addOnSuccessListener(aVoid -> {
                    // Change password
                    user.updatePassword(newPassword)
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(EditProfileActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                                etCurrentPassword.setText("");
                                etNewPassword.setText("");
                                etConfirmNewPassword.setText("");
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(EditProfileActivity.this, "Failed to change password: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProfileActivity.this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                });
    }
}
