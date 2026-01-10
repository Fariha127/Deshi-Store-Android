package com.fariha.deshistore;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class InitializeTestAccounts {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;
    private Context context;

    public InitializeTestAccounts(Context context) {
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseFirestore.getInstance();
    }

    public void createTestVendorAccounts() {
        createTestCompanyVendors();
        createTestRetailVendors();
    }

    public void createTestCompanyVendors() {
        // Akij Food & Beverage Ltd.
        createCompanyVendor(
                "contact@akijfood.com",
                "password123",
                "Mohammed Akij",
                "Business Manager",
                "Akij Food & Beverage Ltd.",
                "+8801711111111",
                "REG-2024-001",
                "BSTI-10001",
                "Akij House, 198 Bir Uttam Mir Shawkat Sarak, Dhaka",
                "TIN-123456001"
        );

        // Anfords Bangladesh Ltd.
        createCompanyVendor(
                "contact@anfords.com",
                "password123",
                "Rashid Ahmed",
                "Sales Director",
                "Anfords Bangladesh Ltd.",
                "+8801722222222",
                "REG-2024-002",
                "BSTI-10002",
                "House 45, Road 10, Sector 4, Uttara, Dhaka",
                "TIN-123456002"
        );

        // Square Toiletries Ltd.
        createCompanyVendor(
                "contact@squaretoiletries.com",
                "password123",
                "Tahmina Begum",
                "Marketing Manager",
                "Square Toiletries Ltd.",
                "+8801733333333",
                "REG-2024-003",
                "BSTI-10003",
                "Square Centre, 48 Mohakhali C/A, Dhaka",
                "TIN-123456003"
        );

        // Sajeeb Group
        createCompanyVendor(
                "contact@sajeeb.com",
                "password123",
                "Sajeeb Wazed",
                "Managing Director",
                "Sajeeb Group",
                "+8801744444444",
                "REG-2024-004",
                "BSTI-10004",
                "Gulshan Avenue, Dhaka",
                "TIN-123456004"
        );

        // PRAN-RFL Group
        createCompanyVendor(
                "contact@pranrfl.com",
                "password123",
                "Ahsan Khan",
                "Regional Manager",
                "PRAN-RFL Group",
                "+8801755555555",
                "REG-2024-005",
                "BSTI-10005",
                "PRAN Centre, Narsingdi",
                "TIN-123456005"
        );

        // Square Food & Beverage Limited
        createCompanyVendor(
                "contact@squarefood.com",
                "password123",
                "Kamal Hossain",
                "Brand Manager",
                "Square Food & Beverage Limited",
                "+8801766666666",
                "REG-2024-006",
                "BSTI-10006",
                "Square Centre, 48 Mohakhali C/A, Dhaka",
                "TIN-123456006"
        );

        // Bashundhara Paper Mills PLC
        createCompanyVendor(
                "contact@bashundhara.com",
                "password123",
                "Ahmed Bashir",
                "Operations Manager",
                "Bashundhara Paper Mills PLC",
                "+8801777777777",
                "REG-2024-007",
                "BSTI-10007",
                "Bashundhara City, Panthapath, Dhaka",
                "TIN-123456007"
        );

        // PRAN Dairy Ltd.
        createCompanyVendor(
                "contact@prandairy.com",
                "password123",
                "Salma Akter",
                "Product Manager",
                "PRAN Dairy Ltd.",
                "+8801788888888",
                "REG-2024-008",
                "BSTI-10008",
                "PRAN Centre, Narsingdi",
                "TIN-123456008"
        );
    }

    private void createCompanyVendor(String email, String password, String fullName,
                                      String designation, String companyName, String phoneNumber,
                                      String registrationNumber, String bstiNumber,
                                      String address, String tinNumber) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().getUser() != null) {
                        String userId = task.getResult().getUser().getUid();
                        android.util.Log.d("InitAccounts", "Created company vendor user: " + email + " with ID: " + userId);

                        Map<String, Object> vendorData = new HashMap<>();
                        vendorData.put("fullName", fullName);
                        vendorData.put("designation", designation);
                        vendorData.put("companyName", companyName);
                        vendorData.put("email", email);
                        vendorData.put("companyEmail", email);
                        vendorData.put("phoneNumber", phoneNumber);
                        vendorData.put("registrationNumber", registrationNumber);
                        vendorData.put("bstiNumber", bstiNumber);
                        vendorData.put("address", address);
                        vendorData.put("tinNumber", tinNumber);
                        vendorData.put("userType", "Company Vendor");

                        mDatabase.collection("users").document(userId).set(vendorData)
                                .addOnSuccessListener(aVoid -> {
                                    android.util.Log.d("InitAccounts", "Saved company vendor data for: " + email);
                                    Toast.makeText(context, "✓ " + companyName, Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    android.util.Log.e("InitAccounts", "Failed to save: " + email + " - " + e.getMessage());
                                    Toast.makeText(context, "✗ Failed: " + email, Toast.LENGTH_SHORT).show();
                                });

                        // Sign out after creating account
                        mAuth.signOut();
                    } else {
                        // Account already exists - try to save data anyway with a known UID
                        android.util.Log.d("InitAccounts", "Account exists, attempting to find and update data for: " + email);
                        
                        // Sign in to get the UID, then save data
                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnSuccessListener(authResult -> {
                                    if (authResult.getUser() != null) {
                                        String userId = authResult.getUser().getUid();
                                        android.util.Log.d("InitAccounts", "Found existing user ID: " + userId + " for " + email);
                                        
                                        Map<String, Object> vendorData = new HashMap<>();
                                        vendorData.put("fullName", fullName);
                                        vendorData.put("designation", designation);
                                        vendorData.put("companyName", companyName);
                                        vendorData.put("email", email);
                                        vendorData.put("companyEmail", email);
                                        vendorData.put("phoneNumber", phoneNumber);
                                        vendorData.put("registrationNumber", registrationNumber);
                                        vendorData.put("bstiNumber", bstiNumber);
                                        vendorData.put("address", address);
                                        vendorData.put("tinNumber", tinNumber);
                                        vendorData.put("userType", "Company Vendor");
                                        
                                        mDatabase.collection("users").document(userId).set(vendorData)
                                                .addOnSuccessListener(aVoid -> {
                                                    android.util.Log.d("InitAccounts", "Updated data for existing account: " + email);
                                                    Toast.makeText(context, "✓ Updated: " + companyName, Toast.LENGTH_SHORT).show();
                                                    mAuth.signOut();
                                                })
                                                .addOnFailureListener(e -> {
                                                    android.util.Log.e("InitAccounts", "Failed to update: " + email + " - " + e.getMessage());
                                                    Toast.makeText(context, "✗ Update failed: " + email, Toast.LENGTH_SHORT).show();
                                                    mAuth.signOut();
                                                });
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    android.util.Log.e("InitAccounts", "Could not sign in to update: " + email + " - " + e.getMessage());
                                    Toast.makeText(context, "Already exists: " + companyName, Toast.LENGTH_SHORT).show();
                                });
                    }
                });
    }

    public void createTestRetailVendors() {
        // Retail Vendor 1 - Dhaka Grocery Store
        createRetailVendor(
                "vendor1@findingbd.com",
                "password123",
                "Karim Rahman",
                "Dhaka Grocery Store",
                "+8801811111111",
                "RET-REG-001",
                "TL-001",
                "Shop 12, Mohammadpur Bazaar, Dhaka",
                "TIN-RET001"
        );

        // Retail Vendor 2 - Chattogram Super Shop
        createRetailVendor(
                "vendor2@findingbd.com",
                "password123",
                "Fatema Khatun",
                "Chattogram Super Shop",
                "+8801822222222",
                "RET-REG-002",
                "TL-002",
                "45 Agrabad Commercial Area, Chattogram",
                "TIN-RET002"
        );

        // Retail Vendor 3 - Sylhet Mini Mart
        createRetailVendor(
                "vendor3@findingbd.com",
                "password123",
                "Rahim Miah",
                "Sylhet Mini Mart",
                "+8801833333333",
                "RET-REG-003",
                "TL-003",
                "Zindabazar Main Road, Sylhet",
                "TIN-RET003"
        );
    }

    private void createRetailVendor(String email, String password, String ownerName,
                                    String shopName, String phoneNumber, String registrationNumber,
                                    String tradeLicense, String shopAddress, String tinNumber) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().getUser() != null) {
                        String userId = task.getResult().getUser().getUid();
                        android.util.Log.d("InitAccounts", "Created retail vendor user: " + email + " with ID: " + userId);

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

                        mDatabase.collection("users").document(userId).set(vendorData)
                                .addOnSuccessListener(aVoid -> {
                                    android.util.Log.d("InitAccounts", "Saved retail vendor data for: " + email);
                                    Toast.makeText(context, "✓ " + shopName, Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    android.util.Log.e("InitAccounts", "Failed to save: " + email + " - " + e.getMessage());
                                    Toast.makeText(context, "✗ Failed: " + email, Toast.LENGTH_SHORT).show();
                                });

                        // Sign out after creating account
                        mAuth.signOut();
                    } else {
                        // Account already exists - try to save data anyway with a known UID
                        android.util.Log.d("InitAccounts", "Account exists, attempting to find and update data for: " + email);
                        
                        // Sign in to get the UID, then save data
                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnSuccessListener(authResult -> {
                                    if (authResult.getUser() != null) {
                                        String userId = authResult.getUser().getUid();
                                        android.util.Log.d("InitAccounts", "Found existing user ID: " + userId + " for " + email);
                                        
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
                                        
                                        mDatabase.collection("users").document(userId).set(vendorData)
                                                .addOnSuccessListener(aVoid -> {
                                                    android.util.Log.d("InitAccounts", "Updated data for existing account: " + email);
                                                    Toast.makeText(context, "✓ Updated: " + shopName, Toast.LENGTH_SHORT).show();
                                                    mAuth.signOut();
                                                })
                                                .addOnFailureListener(e -> {
                                                    android.util.Log.e("InitAccounts", "Failed to update: " + email + " - " + e.getMessage());
                                                    Toast.makeText(context, "✗ Update failed: " + email, Toast.LENGTH_SHORT).show();
                                                    mAuth.signOut();
                                                });
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    android.util.Log.e("InitAccounts", "Could not sign in to update: " + email + " - " + e.getMessage());
                                    Toast.makeText(context, "Already exists: " + shopName, Toast.LENGTH_SHORT).show();
                                });
                    }
                });
    }
}
