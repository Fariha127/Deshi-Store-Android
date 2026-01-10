package com.fariha.deshistore;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class InitializeTestAccounts {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Context context;

    public InitializeTestAccounts(Context context) {
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void createTestVendorAccounts() {
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

                        Map<String, Object> vendorData = new HashMap<>();
                        vendorData.put("fullName", fullName);
                        vendorData.put("designation", designation);
                        vendorData.put("companyName", companyName);
                        vendorData.put("companyEmail", email);
                        vendorData.put("phoneNumber", phoneNumber);
                        vendorData.put("registrationNumber", registrationNumber);
                        vendorData.put("bstiNumber", bstiNumber);
                        vendorData.put("address", address);
                        vendorData.put("tinNumber", tinNumber);
                        vendorData.put("userType", "Company Vendor");

                        mDatabase.child("users").child(userId).setValue(vendorData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(context, "Created vendor: " + email, Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Failed to save data for: " + email, Toast.LENGTH_SHORT).show();
                                });

                        // Sign out after creating account
                        mAuth.signOut();
                    } else {
                        // Account might already exist, which is okay
                        if (task.getException() != null && 
                            task.getException().getMessage().contains("already in use")) {
                            Toast.makeText(context, "Vendor account already exists: " + email, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
