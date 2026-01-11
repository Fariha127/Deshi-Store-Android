package com.fariha.deshistore;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddProductActivity extends AppCompatActivity {

    private EditText etProductName, etManufacturer, etDescription, etPrice, etUnit;
    private Spinner spinnerCategory;
    private ImageView ivImagePreview;
    private Button btnChangeImage, btnSubmit, btnCancel;
    private Uri selectedImageUri;
    private String vendorCompanyName = "";
    
    private FirebaseFirestore mDatabase;
    private FirebaseAuth mAuth;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;
    
    // Max image size for Base64 storage (compress to ~500KB)
    private static final int MAX_IMAGE_DIMENSION = 800;
    private static final int JPEG_QUALITY = 70;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();

        initializeViews();
        setupCategorySpinner();
        setupClickListeners();
        loadVendorCompanyName();
    }

    private void initializeViews() {
        etProductName = findViewById(R.id.etProductName);
        etManufacturer = findViewById(R.id.etManufacturer);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        etUnit = findViewById(R.id.etUnit);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        ivImagePreview = findViewById(R.id.ivImagePreview);
        btnChangeImage = findViewById(R.id.btnChangeImage);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnCancel = findViewById(R.id.btnCancel);
        
        // Make manufacturer field read-only
        etManufacturer.setEnabled(false);
        etManufacturer.setFocusable(false);
    }

    private void setupCategorySpinner() {
        String[] categories = {"Beverages", "Snacks", "Personal Care", "Home Care", 
                             "Food & Groceries", "Health & Wellness", "Baby Care", 
                             "Pet Care", "Stationery"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnChangeImage.setOnClickListener(v -> openImagePicker());
        btnSubmit.setOnClickListener(v -> submitProduct());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void loadVendorCompanyName() {
        if (mAuth.getCurrentUser() == null) {
            Log.e("AddProduct", "No authenticated user");
            etManufacturer.setText("Unknown Manufacturer");
            return;
        }
        
        String vendorId = mAuth.getCurrentUser().getUid();
        Log.d("AddProduct", "Loading vendor info for: " + vendorId);
        
        mDatabase.collection("users").document(vendorId).get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        Log.d("AddProduct", "User document found: " + snapshot.getData());
                        
                        // Try multiple possible field names for company/shop name
                        vendorCompanyName = snapshot.getString("companyName");
                        
                        if (vendorCompanyName == null || vendorCompanyName.isEmpty()) {
                            vendorCompanyName = snapshot.getString("shopName");
                        }
                        
                        if (vendorCompanyName == null || vendorCompanyName.isEmpty()) {
                            vendorCompanyName = snapshot.getString("name");
                        }
                        
                        if (vendorCompanyName == null || vendorCompanyName.isEmpty()) {
                            vendorCompanyName = snapshot.getString("businessName");
                        }
                        
                        if (vendorCompanyName != null && !vendorCompanyName.isEmpty()) {
                            etManufacturer.setText(vendorCompanyName);
                            Log.d("AddProduct", "Set manufacturer to: " + vendorCompanyName);
                        } else {
                            // Use email as fallback
                            String email = mAuth.getCurrentUser().getEmail();
                            if (email != null && email.contains("@")) {
                                vendorCompanyName = email.split("@")[0];
                                etManufacturer.setText(vendorCompanyName);
                            } else {
                                vendorCompanyName = "Vendor";
                                etManufacturer.setText("Vendor");
                            }
                            Log.d("AddProduct", "Using fallback manufacturer: " + vendorCompanyName);
                        }
                    } else {
                        Log.e("AddProduct", "User document not found");
                        vendorCompanyName = "Unknown Manufacturer";
                        etManufacturer.setText(vendorCompanyName);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("AddProduct", "Failed to load vendor info", e);
                    Toast.makeText(this, "Failed to load vendor info: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    vendorCompanyName = "Unknown";
                    etManufacturer.setText(vendorCompanyName);
                });
    }

    private void openImagePicker() {
        // Check for permission based on Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ uses READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_REQUEST_CODE);
                return;
            }
        } else {
            // Older versions use READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
                return;
            }
        }
        
        // Permission granted, open picker
        launchImagePicker();
    }
    
    private void launchImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                          @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchImagePicker();
            } else {
                Toast.makeText(this, "Permission denied. Cannot select image.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                ivImagePreview.setImageURI(selectedImageUri);
                ivImagePreview.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Image selected successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to get image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void submitProduct() {
        String name = etProductName.getText().toString().trim();
        // Use the vendor's company name as manufacturer
        String manufacturer = vendorCompanyName;
        String description = etDescription.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String unit = etUnit.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty() || unit.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable submit button to prevent double submissions
        btnSubmit.setEnabled(false);
        btnSubmit.setText("Processing...");
        
        String vendorId = mAuth.getCurrentUser().getUid();
        String productId = mDatabase.collection("products").document().getId();

        Log.d("AddProduct", "Processing product: " + productId);

        try {
            // Convert image to compressed bitmap
            Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
            
            // Resize image to reduce size
            Bitmap resizedBitmap = resizeBitmap(originalBitmap, MAX_IMAGE_DIMENSION);
            
            // Convert to Base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, baos);
            byte[] imageBytes = baos.toByteArray();
            String base64Image = "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.DEFAULT);
            
            Log.d("AddProduct", "Image converted to Base64, size: " + base64Image.length() + " chars");
            
            // Check if image is too large for Firestore (max ~1MB per document)
            if (base64Image.length() > 900000) {
                btnSubmit.setEnabled(true);
                btnSubmit.setText("Submit Product");
                Toast.makeText(this, "Image too large. Please select a smaller image.", Toast.LENGTH_LONG).show();
                return;
            }
            
            // Save directly to Firestore with Base64 image
            saveProductToDatabase(productId, name, manufacturer, description, 
                    price, unit, category, base64Image, vendorId);
                    
        } catch (IOException e) {
            Log.e("AddProduct", "Failed to read image", e);
            btnSubmit.setEnabled(true);
            btnSubmit.setText("Submit Product");
            Toast.makeText(this, "Failed to read image: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e("AddProduct", "Submit product error", e);
            btnSubmit.setEnabled(true);
            btnSubmit.setText("Submit Product");
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private Bitmap resizeBitmap(Bitmap original, int maxDimension) {
        int width = original.getWidth();
        int height = original.getHeight();
        
        if (width <= maxDimension && height <= maxDimension) {
            return original;
        }
        
        float ratio = Math.min((float) maxDimension / width, (float) maxDimension / height);
        int newWidth = Math.round(width * ratio);
        int newHeight = Math.round(height * ratio);
        
        return Bitmap.createScaledBitmap(original, newWidth, newHeight, true);
    }

    private void saveProductToDatabase(String productId, String name, String manufacturer,
                                      String description, double price, String unit,
                                      String category, String imageUrl, String vendorId) {
        Map<String, Object> productData = new HashMap<>();
        productData.put("name", name);
        productData.put("manufacturer", manufacturer);
        productData.put("description", description);
        productData.put("price", price);
        productData.put("unit", unit);
        productData.put("category", category);
        productData.put("imageUrl", imageUrl);
        productData.put("vendorId", vendorId);
        productData.put("status", "pending");
        productData.put("type", "vendor");
        productData.put("createdAt", System.currentTimeMillis());

        mDatabase.collection("products").document(productId).set(productData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Product submitted for approval", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save product", Toast.LENGTH_SHORT).show();
                });
    }
}
