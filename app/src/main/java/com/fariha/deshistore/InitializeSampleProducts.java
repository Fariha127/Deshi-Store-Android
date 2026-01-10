package com.fariha.deshistore;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class InitializeSampleProducts {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;
    private Context context;

    public InitializeSampleProducts(Context context) {
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseFirestore.getInstance();
    }

    public void createSampleProducts() {
        // First, get vendor UIDs by signing in
        createProductsForVendor("contact@akijfood.com", "password123", "Akij Food & Beverage Ltd.", new String[][]{
            {"Mojo", "Carbonated lemon-lime flavored energy drink", "30", "500ml bottle", "Beverages"},
            {"Spa Drinking Water", "Purified bottled drinking water", "20", "1L bottle", "Beverages"}
        });

        createProductsForVendor("contact@pranrfl.com", "password123", "PRAN-RFL Group", new String[][]{
            {"Frooto", "Delicious mango juice drink", "35", "250ml pack", "Beverages"},
            {"PRAN Chips", "Crispy potato chips with masala flavor", "20", "25g pack", "Snacks"}
        });

        createProductsForVendor("contact@squaretoiletries.com", "password123", "Square Toiletries Ltd.", new String[][]{
            {"Meril Baby Soap", "Gentle soap for baby's sensitive skin", "45", "75g bar", "Personal Care"},
            {"Senora Sanitary Napkin", "Ultra-thin sanitary pads", "180", "8 pads pack", "Personal Care"}
        });

        createProductsForVendor("contact@sajeebgroup.com", "password123", "Sajeeb Group", new String[][]{
            {"Fresh Soybean Oil", "Refined soybean cooking oil", "180", "1L bottle", "Food & Groceries"}
        });

        createProductsForVendor("contact@squarefood.com", "password123", "Square Food & Beverage Limited", new String[][]{
            {"Ruchi Mango Juice", "100% natural mango juice", "80", "500ml pack", "Beverages"}
        });

        createProductsForVendor("contact@bashundharapaper.com", "password123", "Bashundhara Paper Mills PLC", new String[][]{
            {"Bashundhara Tissue", "Soft facial tissue paper", "25", "100 sheets pack", "Home Care"}
        });

        createProductsForVendor("contact@prandairy.com", "password123", "PRAN Dairy Ltd.", new String[][]{
            {"PRAN Milk", "Fresh pasteurized full cream milk", "60", "500ml pack", "Food & Groceries"}
        });

        // Retail vendors
        createProductsForVendor("vendor1@findingbd.com", "password123", "Dhaka Grocery Hub", new String[][]{
            {"Mixed Spices", "Traditional Bangladeshi spice mix", "120", "200g pack", "Food & Groceries"}
        });

        createProductsForVendor("vendor2@findingbd.com", "password123", "Chattogram Super Shop", new String[][]{
            {"Local Honey", "Pure natural honey from Chittagong hills", "450", "500g jar", "Food & Groceries"}
        });

        createProductsForVendor("vendor3@findingbd.com", "password123", "Sylhet Mini Mart", new String[][]{
            {"Sylhet Tea", "Premium black tea from Sylhet gardens", "250", "250g pack", "Beverages"}
        });
    }

    private void createProductsForVendor(String email, String password, String manufacturer, String[][] products) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    if (authResult.getUser() != null) {
                        String vendorId = authResult.getUser().getUid();
                        android.util.Log.d("InitProducts", "Creating products for: " + email + " (ID: " + vendorId + ")");

                        for (String[] productData : products) {
                            String productId = mDatabase.collection("products").document().getId();
                            
                            Map<String, Object> product = new HashMap<>();
                            product.put("name", productData[0]);
                            product.put("manufacturer", manufacturer);
                            product.put("description", productData[1]);
                            product.put("price", Double.parseDouble(productData[2]));
                            product.put("unit", productData[3]);
                            product.put("category", productData[4]);
                            product.put("imageUrl", "https://via.placeholder.com/300x300?text=" + productData[0].replace(" ", "+"));
                            product.put("vendorId", vendorId);
                            product.put("status", "approved");
                            product.put("type", "vendor");
                            product.put("createdAt", System.currentTimeMillis());

                            mDatabase.collection("products").document(productId).set(product)
                                    .addOnSuccessListener(aVoid -> {
                                        android.util.Log.d("InitProducts", "✓ Created: " + productData[0]);
                                        Toast.makeText(context, "✓ " + productData[0], Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        android.util.Log.e("InitProducts", "✗ Failed: " + productData[0] + " - " + e.getMessage());
                                    });
                        }
                        
                        mAuth.signOut();
                    }
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("InitProducts", "Could not sign in: " + email + " - " + e.getMessage());
                });
    }
}
