package com.fariha.deshistore;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class AddProductActivity extends AppCompatActivity {

    private EditText etProductName, etManufacturer, etDescription, etPrice, etUnit;
    private Spinner spinnerCategory;
    private ImageView ivImagePreview;
    private Button btnChangeImage, btnSubmit, btnCancel;
    private Uri selectedImageUri;
    private String vendorCompanyName = "";
    
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private FirebaseAuth mAuth;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

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
        String vendorId = mAuth.getCurrentUser().getUid();
        mDatabase.child("users").child(vendorId).get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        vendorCompanyName = snapshot.child("companyName").getValue(String.class);
                        if (vendorCompanyName == null || vendorCompanyName.isEmpty()) {
                            // For retail vendors, use shopName
                            vendorCompanyName = snapshot.child("shopName").getValue(String.class);
                        }
                        if (vendorCompanyName != null && !vendorCompanyName.isEmpty()) {
                            etManufacturer.setText(vendorCompanyName);
                        } else {
                            etManufacturer.setText("Unknown Manufacturer");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load vendor information", Toast.LENGTH_SHORT).show();
                });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            ivImagePreview.setImageURI(selectedImageUri);
            ivImagePreview.setVisibility(View.VISIBLE);
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

        String vendorId = mAuth.getCurrentUser().getUid();
        String productId = mDatabase.child("products").push().getKey();

        // Upload image first
        StorageReference imageRef = mStorage.child("products/" + productId + ".jpg");
        imageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            saveProductToDatabase(productId, name, manufacturer, description, 
                                    price, unit, category, uri.toString(), vendorId);
                        }))
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to upload image: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                });
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

        mDatabase.child("products").child(productId).setValue(productData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Product submitted for approval", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save product", Toast.LENGTH_SHORT).show();
                });
    }
}
