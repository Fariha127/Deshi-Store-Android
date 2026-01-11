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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import android.os.Handler;
import android.os.Looper;

public class UserSignUpActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword, etConfirmPassword;
    private EditText etPhoneNumber, etDateOfBirth, etCity;
    private Spinner spinnerGender;
    private Button btnRegister, btnBackToLogin, btnSendCode;
    private ImageView ivTogglePassword, ivToggleConfirmPassword;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

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
            if (!email.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                sendVerificationCode(email);
            } else {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
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

        // Check if email is verified
        if (!EmailVerificationActivity.isEmailVerified(this, email)) {
            Toast.makeText(this, "Please verify your email first by clicking 'Send Code'", Toast.LENGTH_LONG).show();
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

    private void sendVerificationCode(String email) {
        btnSendCode.setEnabled(false);
        Toast.makeText(this, "Sending verification code...", Toast.LENGTH_SHORT).show();

        // Generate verification code
        String verificationCode = EmailSender.generateVerificationCode();

        // Send email
        EmailSender.sendVerificationEmail(email, verificationCode, new EmailSender.EmailCallback() {
            @Override
            public void onSending() {
                // Already showing toast
            }

            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    Toast.makeText(UserSignUpActivity.this, 
                        "Verification code sent to your email!", Toast.LENGTH_SHORT).show();
                    
                    // Launch verification activity
                    Intent intent = new Intent(UserSignUpActivity.this, EmailVerificationActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("code", verificationCode);
                    intent.putExtra("expiry", System.currentTimeMillis() + (10 * 60 * 1000));
                    startActivityForResult(intent, 100);
                    
                    btnSendCode.setEnabled(true);
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(UserSignUpActivity.this, 
                        "Failed to send verification code: " + error, Toast.LENGTH_LONG).show();
                    btnSendCode.setEnabled(true);
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Toast.makeText(this, "Email verified! You can now register.", Toast.LENGTH_SHORT).show();
        }
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

        // Write to Firestore with retry (exponential backoff)
        final int maxAttempts = 3;
        Handler handler = new Handler(Looper.getMainLooper());

        class Writer {
            void write(final int attempt) {
                mFirestore.collection("users")
                        .document(userId)
                        .set(userData)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(UserSignUpActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(UserSignUpActivity.this, LoginActivity.class));
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            if (attempt < maxAttempts) {
                                long delayMs = (long) Math.pow(2, attempt) * 1000L; // 2^attempt seconds
                                handler.postDelayed(() -> write(attempt + 1), delayMs);
                            } else {
                                Toast.makeText(UserSignUpActivity.this, "Registration saved but failed to sync to server. Please contact support.", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(UserSignUpActivity.this, LoginActivity.class));
                                finish();
                            }
                        });
            }
        }

        new Writer().write(0);
    }
}
