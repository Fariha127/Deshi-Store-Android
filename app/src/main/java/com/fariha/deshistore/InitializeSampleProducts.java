package com.fariha.deshistore;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InitializeSampleProducts {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;
    private Context context;
    private Handler handler;
    private int currentVendorIndex = 0;
    private List<VendorData> vendorsList;

    private static class VendorData {
        String email;
        String password;
        String manufacturer;
        String[][] products;

        VendorData(String email, String password, String manufacturer, String[][] products) {
            this.email = email;
            this.password = password;
            this.manufacturer = manufacturer;
            this.products = products;
        }
    }

    public InitializeSampleProducts(Context context) {
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseFirestore.getInstance();
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void createSampleProducts() {
        Toast.makeText(context, "Starting product creation...", Toast.LENGTH_LONG).show();
        android.util.Log.d("InitProducts", "=== Starting Product Creation ===");
        
        // Initialize vendors list and start processing
        initializeVendorsList();
    }
    
    private void initializeVendorsList() {
        vendorsList = new ArrayList<>();
        
        // Add all vendors with their products
        vendorsList.add(new VendorData("contact@akijfood.com", "password123", "Akij Food & Beverage Ltd. (AFBL)", new String[][]{
            {"Mojo", "Carbonated lemon-lime flavored energy drink", "25", "250ml", "Soft Drink", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/mojo.jpg"},
            {"Spa Drinking Water", "Pure mineral drinking water", "20", "500ml", "Water", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/spa-water.jpg"}
        }));

        vendorsList.add(new VendorData("contact@anfords.com", "password123", "Anfords Bangladesh Ltd.", new String[][]{
            {"MediPlus DS", "Dental care toothpaste with advanced formula", "85", "100g", "Toothpaste", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/mediplus.jpg"}
        }));

        vendorsList.add(new VendorData("contact@squaretoiletries.com", "password123", "Square Toiletries Ltd.", new String[][]{
            {"Meril Milk Soap", "Gentle moisturizing soap with milk protein", "35", "75g", "Moisturizing Soap", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/meril-soap.jpg"},
            {"Revive Perfect Skin", "Advanced moisturizing lotion for perfect skin", "150", "100ml", "Moisturizing Lotion", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/revive-lotion.jpg"},
            {"Jui HairCare Oil", "Nourishing hair oil with natural ingredients", "95", "200ml", "Hair Oil", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/jui-oil.jpg"}
        }));

        vendorsList.add(new VendorData("contact@sajeeb.com", "password123", "Sajeeb Group", new String[][]{
            {"Shezan Mango Juice", "100% natural mango juice drink", "120", "1L", "Mango Juice", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/shezan-juice.jpg"}
        }));

        vendorsList.add(new VendorData("contact@pranrfl.com", "password123", "Pran Foods Ltd.", new String[][]{
            {"Pran Potata Spicy", "Crispy and spicy potato biscuits", "40", "200g", "Biscuit", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/pran-potata.jpg"},
            {"Ruchi BBQ Chanachur", "Tasty BBQ flavored chanachur snack", "30", "150g", "Snack", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/ruchi-chanachur.jpg"}
        }));

        vendorsList.add(new VendorData("contact@bashundhara.com", "password123", "Bashundhara Paper Mills PLC", new String[][]{
            {"Bashundhara Towel", "Soft and absorbent hand towel", "80", "pack", "Hand Towel", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/bashundhara-towel.jpg"}
        }));

        vendorsList.add(new VendorData("contact@squarefood.com", "password123", "Square Food & Beverage Ltd.", new String[][]{
            {"Radhuni Turmeric", "Pure turmeric powder for cooking", "55", "100g", "Powder", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/radhuni-tumeric.jpg"}
        }));

        vendorsList.add(new VendorData("contact@prandairy.com", "password123", "Pran Dairy Ltd.", new String[][]{
            {"Pran Premium Ghee", "Pure and premium quality cooking ghee", "250", "500g", "Cooking Ghee", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/pran-ghee.jpg"}
        }));
        
        // Start sequential processing
        currentVendorIndex = 0;
        processNextVendor();
    }
    
    private void processNextVendor() {
        if (currentVendorIndex >= vendorsList.size()) {
            // All done
            android.util.Log.d("InitProducts", "=== All Products Created Successfully! ===");
            Toast.makeText(context, "✓ All 12 products created successfully!", Toast.LENGTH_LONG).show();
            return;
        }
        
        VendorData vendor = vendorsList.get(currentVendorIndex);
        android.util.Log.d("InitProducts", "Processing vendor " + (currentVendorIndex + 1) + "/" + vendorsList.size() + ": " + vendor.email);
        
        createProductsForVendor(vendor.email, vendor.password, vendor.manufacturer, vendor.products);
    }

    private void createProductsForVendor(String email, String password, String manufacturer, String[][] products) {
        android.util.Log.d("InitProducts", "Attempting to sign in: " + email);
        
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    if (authResult.getUser() != null) {
                        String vendorId = authResult.getUser().getUid();
                        android.util.Log.d("InitProducts", "✓ Signed in: " + email + " (ID: " + vendorId + ")");
                        
                        // Create products sequentially for this vendor
                        createProductsSequentially(vendorId, manufacturer, products, 0);
                    }
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("InitProducts", "✗ Sign in failed: " + email, e);
                    Toast.makeText(context, "✗ Failed to sign in: " + email, Toast.LENGTH_SHORT).show();
                    
                    // Move to next vendor even if this one fails
                    currentVendorIndex++;
                    handler.postDelayed(this::processNextVendor, 1000);
                });
    }
    
    private void createProductsSequentially(String vendorId, String manufacturer, String[][] products, int index) {
        if (index >= products.length) {
            // All products for this vendor created, sign out and move to next vendor
            android.util.Log.d("InitProducts", "✓ Completed all products for: " + manufacturer);
            mAuth.signOut();
            
            currentVendorIndex++;
            handler.postDelayed(this::processNextVendor, 1500);
            return;
        }
        
        String[] productData = products[index];
        
        // Check if product already exists for this vendor
        mDatabase.collection("products")
                .whereEqualTo("vendorId", vendorId)
                .whereEqualTo("name", productData[0])
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Product already exists, skip it
                        android.util.Log.d("InitProducts", "⊘ Skipping duplicate: " + productData[0]);
                        Toast.makeText(context, "⊘ Already exists: " + productData[0], Toast.LENGTH_SHORT).show();
                        
                        // Move to next product
                        handler.postDelayed(() -> createProductsSequentially(vendorId, manufacturer, products, index + 1), 300);
                    } else {
                        // Product doesn't exist, create it
                        createSingleProduct(vendorId, manufacturer, productData, products, index);
                    }
                })
                .addOnFailureListener(e -> {
                    // If check fails, try to create anyway
                    android.util.Log.e("InitProducts", "Failed to check for duplicate: " + productData[0], e);
                    createSingleProduct(vendorId, manufacturer, productData, products, index);
                });
    }
    
    private void createSingleProduct(String vendorId, String manufacturer, String[] productData, String[][] products, int index) {
        String productId = mDatabase.collection("products").document().getId();
        
        Map<String, Object> product = new HashMap<>();
        product.put("name", productData[0]);
        product.put("manufacturer", manufacturer);
        product.put("description", productData[1]);
        product.put("price", Double.parseDouble(productData[2]));
        product.put("unit", productData[3]);
        product.put("category", productData[4]);
        product.put("imageUrl", productData[5]);
        product.put("vendorId", vendorId);
        product.put("status", "approved");
        product.put("type", "vendor");
        product.put("createdAt", System.currentTimeMillis());
        product.put("recommendCount", 0);

        android.util.Log.d("InitProducts", "Creating: " + productData[0]);
        
        mDatabase.collection("products").document(productId).set(product)
                .addOnSuccessListener(aVoid -> {
                    android.util.Log.d("InitProducts", "✓ Created: " + productData[0]);
                    Toast.makeText(context, "✓ " + productData[0], Toast.LENGTH_SHORT).show();
                    
                    // Create next product for this vendor after a short delay
                    handler.postDelayed(() -> createProductsSequentially(vendorId, manufacturer, products, index + 1), 500);
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("InitProducts", "✗ Failed: " + productData[0], e);
                    Toast.makeText(context, "✗ Failed: " + productData[0], Toast.LENGTH_SHORT).show();
                    
                    // Continue to next product even if this one fails
                    handler.postDelayed(() -> createProductsSequentially(vendorId, manufacturer, products, index + 1), 500);
                });
    }
}
