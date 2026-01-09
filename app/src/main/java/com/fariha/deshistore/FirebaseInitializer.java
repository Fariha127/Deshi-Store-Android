package com.fariha.deshistore;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class FirebaseInitializer {
    private static final String TAG = "FirebaseInitializer";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Context context;

    public FirebaseInitializer(Context context) {
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    // This method should be called once to create all manufacturer accounts
    public void createManufacturerAccounts() {
        createAccount("contact@akijfood.com", "password123", "Akij Food & Beverage Ltd. AFBL");
        createAccount("contact@anfords.com", "password123", "Anfords Bangladesh Ltd.");
        createAccount("contact@squaretoiletries.com", "password123", "Square Toiletries Ltd.");
        createAccount("contact@sajeeb.com", "password123", "Sajeeb Group");
        createAccount("contact@pranrfl.com", "password123", "PRAN-RFL Group");
        createAccount("contact@squarefood.com", "password123", "Square Food & Beverage Limited");
        createAccount("contact@bashundhara.com", "password123", "Bashundhara Paper Mills PLC");
        createAccount("contact@prandairy.com", "password123", "PRAN Dairy Ltd.");
    }

    private void createAccount(final String email, final String password, final String companyName) {
        // First check if already exists by trying to sign in
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Account already exists: " + email);
                        mAuth.signOut();
                    } else {
                        // Account doesn't exist, create it
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(createTask -> {
                                    if (createTask.isSuccessful()) {
                                        String userId = mAuth.getCurrentUser().getUid();
                                        saveCompanyData(userId, email, companyName);
                                        Log.d(TAG, "Account created: " + email);
                                        mAuth.signOut();
                                    } else {
                                        Log.e(TAG, "Failed to create account: " + email, 
                                                createTask.getException());
                                    }
                                });
                    }
                });
    }

    private void saveCompanyData(String userId, String email, String companyName) {
        Map<String, Object> companyData = new HashMap<>();
        companyData.put("email", email);
        companyData.put("companyName", companyName);
        companyData.put("userType", "Company Vendor");
        companyData.put("verified", true);
        companyData.put("fullName", "Authorized Representative");
        companyData.put("designation", "Account Manager");
        companyData.put("phoneNumber", "N/A");

        mDatabase.child("users").child(userId).setValue(companyData)
                .addOnSuccessListener(aVoid -> 
                    Log.d(TAG, "Company data saved: " + companyName))
                .addOnFailureListener(e -> 
                    Log.e(TAG, "Failed to save company data: " + companyName, e));
    }
}
