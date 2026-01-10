package com.fariha.deshistore;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private Spinner spinnerLoginType;
    private EditText etEmail, etPassword;
    private Button btnLogin, btnBackToHome;
    private TextView tvSignUpCompanyVendor, tvSignUpRetailVendor, tvSignUpUser;
    private ImageView ivTogglePassword;
    private boolean isPasswordVisible = false;
    
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    
    private static final String ADMIN_EMAIL = "admin@findingbd.com";
    private static final String ADMIN_PASSWORD = "admin123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        initializeViews();
        setupLoginTypeSpinner();
        setupClickListeners();
    }

    private void initializeViews() {
        spinnerLoginType = findViewById(R.id.spinnerLoginType);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnBackToHome = findViewById(R.id.btnBackToHome);
        tvSignUpCompanyVendor = findViewById(R.id.tvSignUpCompanyVendor);
        tvSignUpRetailVendor = findViewById(R.id.tvSignUpRetailVendor);
        tvSignUpUser = findViewById(R.id.tvSignUpUser);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);
    }

    private void setupLoginTypeSpinner() {
        String[] loginTypes = {"User", "Admin", "Company Vendor", "Retail Vendor"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, loginTypes);
        spinnerLoginType.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> handleLogin());
        
        btnBackToHome.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
        
        tvSignUpCompanyVendor.setOnClickListener(v -> {
            startActivity(new Intent(this, CompanyVendorSignUpActivity.class));
        });
        
        tvSignUpRetailVendor.setOnClickListener(v -> {
            startActivity(new Intent(this, RetailVendorSignUpActivity.class));
        });
        
        tvSignUpUser.setOnClickListener(v -> {
            startActivity(new Intent(this, UserSignUpActivity.class));
        });

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

        // Hidden feature: Long press on "Back to Home" button to initialize test vendor accounts
        btnBackToHome.setOnLongClickListener(v -> {
            Toast.makeText(this, "Initializing test vendor accounts...", Toast.LENGTH_LONG).show();
            new InitializeTestAccounts(this).createTestVendorAccounts();
            return true;
        });
    }

    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String loginType = spinnerLoginType.getSelectedItem().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Handle Admin login
        if (loginType.equals("Admin")) {
            if (email.equals(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD)) {
                Toast.makeText(this, "Admin login successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, AdminDashboardActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Incorrect email or password!", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        // Handle Firebase authentication for other user types
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Verify user type matches login type
                            verifyAndLoginUser(user.getUid(), loginType);
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Incorrect email or password!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void verifyAndLoginUser(String userId, String expectedLoginType) {
        // Add timeout handler
        new android.os.Handler().postDelayed(() -> {
            navigateBasedOnUserType(expectedLoginType);
        }, 5000); // 5 second timeout
        
        mDatabase.child("users").child(userId).get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        String userType = snapshot.child("userType").getValue(String.class);
                        
                        // If userType is null or empty, treat as "User" for backward compatibility
                        if (userType == null || userType.isEmpty()) {
                            userType = "User";
                        }
                        
                        if (userType.equals(expectedLoginType)) {
                            navigateBasedOnUserType(expectedLoginType);
                        } else {
                            Toast.makeText(this, "Invalid login type for this account", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                        }
                    } else {
                        // Still navigate based on expected type since they are authenticated
                        navigateBasedOnUserType(expectedLoginType);
                    }
                })
                .addOnFailureListener(e -> {
                    // Still navigate based on expected type since they are authenticated
                    navigateBasedOnUserType(expectedLoginType);
                });
    }
    
    private void navigateBasedOnUserType(String userType) {
        Intent intent;
        switch (userType) {
            case "Company Vendor":
            case "Retail Vendor":
                intent = new Intent(LoginActivity.this, VendorDashboardActivity.class);
                break;
            case "User":
            default:
                intent = new Intent(LoginActivity.this, HomeActivity.class);
                break;
        }
        startActivity(intent);
        finish();
    }
}
