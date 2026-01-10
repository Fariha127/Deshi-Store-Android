package com.fariha.deshistore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboardActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private Button btnLogout, btnInitAccounts, btnInitProducts;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        mAuth = FirebaseAuth.getInstance();

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        btnLogout = findViewById(R.id.btnLogout);
        btnInitAccounts = findViewById(R.id.btnInitAccounts);
        btnInitProducts = findViewById(R.id.btnInitProducts);

        // Setup ViewPager with adapter
        AdminPagerAdapter adapter = new AdminPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Users");
                            break;
                        case 1:
                            tab.setText("Company Vendors");
                            break;
                        case 2:
                            tab.setText("Retail Vendors");
                            break;
                        case 3:
                            tab.setText("Product Approvals");
                            break;
                    }
                }
        ).attach();

        btnInitAccounts.setOnClickListener(v -> {
            Toast.makeText(this, "Initializing vendor accounts... Please wait", Toast.LENGTH_LONG).show();
            android.util.Log.d("AdminDashboard", "Starting account initialization...");
            
            new InitializeTestAccounts(this).createTestVendorAccounts();
            
            // Check database after 5 seconds
            new android.os.Handler().postDelayed(() -> {
                checkDatabaseAccounts();
            }, 5000);
        });

        btnInitProducts.setOnClickListener(v -> {
            Toast.makeText(this, "Initializing sample products... Please wait", Toast.LENGTH_LONG).show();
            android.util.Log.d("AdminDashboard", "Starting product initialization...");
            
            new InitializeSampleProducts(this).createSampleProducts();
            
            new android.os.Handler().postDelayed(() -> {
                Toast.makeText(this, "Sample products created! Check vendor dashboards.", Toast.LENGTH_LONG).show();
            }, 5000);
        });

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void checkDatabaseAccounts() {
        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int totalUsers = 0;
                    int companyVendors = 0;
                    int retailVendors = 0;
                    int regularUsers = 0;
                    
                    android.util.Log.d("AdminDashboard", "=== DATABASE CHECK ===");
                    android.util.Log.d("AdminDashboard", "Total accounts in database: " + queryDocumentSnapshots.size());
                    
                    for (com.google.firebase.firestore.QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        totalUsers++;
                        String userType = document.getString("userType");
                        String email = document.getString("email");
                        String companyName = document.getString("companyName");
                        String shopName = document.getString("shopName");
                        
                        android.util.Log.d("AdminDashboard", "User " + totalUsers + ": Type=" + userType + 
                                ", Email=" + email + 
                                ", Company=" + companyName + 
                                ", Shop=" + shopName);
                        
                        if ("Company Vendor".equals(userType)) {
                            companyVendors++;
                        } else if ("Retail Vendor".equals(userType)) {
                            retailVendors++;
                        } else if ("User".equals(userType)) {
                            regularUsers++;
                        }
                    }
                    
                    String message = "Database has:\n" +
                            companyVendors + " Company Vendors\n" +
                            retailVendors + " Retail Vendors\n" +
                            regularUsers + " Regular Users";
                    
                    android.util.Log.d("AdminDashboard", "=== SUMMARY ===");
                    android.util.Log.d("AdminDashboard", message);
                    
                    Toast.makeText(AdminDashboardActivity.this, message, Toast.LENGTH_LONG).show();
                    
                    if (companyVendors == 0 && retailVendors == 0) {
                        Toast.makeText(AdminDashboardActivity.this, 
                                "No vendors found! Check Firestore rules and account creation.", 
                                Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("AdminDashboard", "Database check error: " + e.getMessage());
                    Toast.makeText(AdminDashboardActivity.this, 
                            "Error checking database: " + e.getMessage(), 
                            Toast.LENGTH_LONG).show();
                });
    }
}
