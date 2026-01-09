package com.fariha.deshistore;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UserSignUpActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword, etConfirmPassword;
    private EditText etPhoneNumber, etDateOfBirth, etCity;
    private Spinner spinnerGender;
    private Button btnRegister, btnBackToLogin, btnSendCode;
    private ImageView ivTogglePassword, ivToggleConfirmPassword;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        initializeViews();
        setupGenderSpinner();
        setupClickListeners();
    }

    private void initializeViews() {
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        etCity = findViewById(R.id.etCity);
        spinnerGender = findViewById(R.id.spinnerGender);
        btnRegister = findViewById(R.id.btnRegister);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);
        btnSendCode = findViewById(R.id.btnSendCode);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        ivToggleConfirmPassword = findViewById(R.id.ivToggleConfirmPassword);
    }

    private void setupGenderSpinner() {
        String[] genders = {"Select Gender", "Male", "Female", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, genders);
        spinnerGender.setAdapter(adapter);
    }

    private void setupClickListeners() {
        etDateOfBirth.setOnClickListener(v -> showDatePicker());
        
        ivTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                isPasswordVisible = false;
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                isPasswordVisible = true;
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        ivToggleConfirmPassword.setOnClickListener(v -> {
            if (isConfirmPasswordVisible) {
                etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                isConfirmPasswordVisible = false;
            } else {
                etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                isConfirmPasswordVisible = true;
            }
            etConfirmPassword.setSelection(etConfirmPassword.getText().length());
        });
        
        btnSendCode.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (!email.isEmpty()) {
                Toast.makeText(this, "Verification code sent to " + email, Toast.LENGTH_SHORT).show();
                // TODO: Implement email verification
            } else {
                Toast.makeText(this, "Please enter email first", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnRegister.setOnClickListener(v -> handleRegistration());
        
        btnBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    etDateOfBirth.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void handleRegistration() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String dateOfBirth = etDateOfBirth.getText().toString().trim();
        String gender = spinnerGender.getSelectedItem().toString();
        String city = etCity.getText().toString().trim();

        // Validation
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || 
            confirmPassword.isEmpty() || phoneNumber.isEmpty() || dateOfBirth.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (gender.equals("Select Gender")) {
            Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show();
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
                            saveUserData(user.getUid(), fullName, email, phoneNumber, 
                                       dateOfBirth, gender, city);
                        }
                    } else {
                        Toast.makeText(this, "Registration failed: " + 
                                task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserData(String userId, String fullName, String email, String phoneNumber,
                              String dateOfBirth, String gender, String city) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("fullName", fullName);
        userData.put("email", email);
        userData.put("phoneNumber", phoneNumber);
        userData.put("dateOfBirth", dateOfBirth);
        userData.put("gender", gender);
        userData.put("city", city);
        userData.put("userType", "User");

        mDatabase.child("users").child(userId).setValue(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                });
    }
}
