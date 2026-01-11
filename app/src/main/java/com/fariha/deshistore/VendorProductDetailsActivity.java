package com.fariha.deshistore;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VendorProductDetailsActivity extends AppCompatActivity {

    private TextView tvTitle, tvStatus, tvRejectionReason;
    private EditText etProductName, etManufacturer, etDescription, etPrice, etUnit;
    private Spinner spinnerCategory;
    private ImageView ivImagePreview;
    private Button btnChangeImage, btnSave, btnCancel, btnDelete, btnEdit;
    private Uri selectedImageUri;
    private String productId;
    private Product currentProduct;
    private boolean isEditMode = false;
    
    private FirebaseFirestore mDatabase;
    private FirebaseAuth mAuth;
    private static final int PICK_IMAGE_REQUEST = 1;
    
    // Image settings for Base64 storage
    private static final int MAX_IMAGE_DIMENSION = 800;
    private static final int JPEG_QUALITY = 70;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_product_details);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();

        productId = getIntent().getStringExtra("product_id");
        Log.d("VendorProductDetails", "Loading product with ID: " + productId);
        
        if (productId == null || productId.isEmpty()) {
            Toast.makeText(this, "Invalid product", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupCategorySpinner();
        loadProductData();
        setupClickListeners();
    }

    private void initializeViews() {
        tvTitle = findViewById(R.id.tvTitle);
        tvStatus = findViewById(R.id.tvStatus);
        tvRejectionReason = findViewById(R.id.tvRejectionReason);
        etProductName = findViewById(R.id.etProductName);
        etManufacturer = findViewById(R.id.etManufacturer);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        etUnit = findViewById(R.id.etUnit);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        ivImagePreview = findViewById(R.id.ivImagePreview);
        btnChangeImage = findViewById(R.id.btnChangeImage);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        btnDelete = findViewById(R.id.btnDelete);
        btnEdit = findViewById(R.id.btnEdit);
        
        // Initially set to view mode
        setViewMode();
    }

    private void setupCategorySpinner() {
        String[] categories = {"Beverages", "Snacks", "Personal Care", "Home Care", 
                             "Food & Groceries", "Health & Wellness", "Baby Care", 
                             "Pet Care", "Stationery"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(adapter);
    }

    private void loadProductData() {
        mDatabase.collection("products").document(productId).get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        currentProduct = snapshot.toObject(Product.class);
                        if (currentProduct != null) {
                            currentProduct.setProductId(snapshot.getId());
                            displayProductData();
                        }
                    } else {
                        Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load product: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void displayProductData() {
        if (currentProduct == null) return;

        etProductName.setText(currentProduct.getName());
        etManufacturer.setText(currentProduct.getManufacturer());
        etDescription.setText(currentProduct.getDescription());
        etPrice.setText(String.valueOf(currentProduct.getPrice()));
        etUnit.setText(currentProduct.getUnit());
        
        // Set category
        String category = currentProduct.getCategory();
        if (category != null) {
            ArrayAdapter adapter = (ArrayAdapter) spinnerCategory.getAdapter();
            int position = adapter.getPosition(category);
            if (position >= 0) {
                spinnerCategory.setSelection(position);
            }
        }
        
        // Load image
        if (currentProduct.getImageUrl() != null && !currentProduct.getImageUrl().isEmpty()) {
            String imageUrl = currentProduct.getImageUrl();
            if (imageUrl.startsWith("data:image")) {
                try {
                    String base64Data = imageUrl.substring(imageUrl.indexOf(",") + 1);
                    byte[] decodedBytes = android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT);
                    android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    ivImagePreview.setImageBitmap(bitmap);
                } catch (Exception e) {
                    ivImagePreview.setImageResource(R.drawable.ic_launcher_background);
                }
            } else {
                Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(ivImagePreview);
            }
            ivImagePreview.setVisibility(View.VISIBLE);
        }
        
        // Display status
        String status = currentProduct.getStatus() != null ? currentProduct.getStatus().toUpperCase() : "PENDING";
        tvStatus.setText("Status: " + status);
        
        // Set status color
        switch (status) {
            case "APPROVED":
                tvStatus.setTextColor(0xFF4CAF50); // Green
                tvRejectionReason.setVisibility(View.GONE);
                break;
            case "REJECTED":
                tvStatus.setTextColor(0xFFF44336); // Red
                if (currentProduct.getRejectionReason() != null) {
                    tvRejectionReason.setVisibility(View.VISIBLE);
                    tvRejectionReason.setText("Rejection Reason: " + currentProduct.getRejectionReason());
                }
                break;
            case "PENDING":
            default:
                tvStatus.setTextColor(0xFFFF9800); // Orange
                tvRejectionReason.setVisibility(View.GONE);
                break;
        }
    }

    private void setupClickListeners() {
        btnEdit.setOnClickListener(v -> setEditMode());
        
        btnChangeImage.setOnClickListener(v -> {
            if (isEditMode) {
                openImagePicker();
            }
        });
        
        btnSave.setOnClickListener(v -> saveProduct());
        
        btnCancel.setOnClickListener(v -> {
            if (isEditMode) {
                setViewMode();
                displayProductData(); // Restore original data
            } else {
                finish();
            }
        });
        
        btnDelete.setOnClickListener(v -> confirmDeleteProduct());
    }

    private void setViewMode() {
        isEditMode = false;
        tvTitle.setText("Product Details");
        
        etProductName.setEnabled(false);
        etManufacturer.setEnabled(false);
        etDescription.setEnabled(false);
        etPrice.setEnabled(false);
        etUnit.setEnabled(false);
        spinnerCategory.setEnabled(false);
        btnChangeImage.setEnabled(false);
        
        btnEdit.setVisibility(View.VISIBLE);
        btnSave.setVisibility(View.GONE);
        btnDelete.setVisibility(View.GONE);
        btnCancel.setText("Back");
    }

    private void setEditMode() {
        isEditMode = true;
        tvTitle.setText("Edit Product");
        
        etProductName.setEnabled(true);
        etDescription.setEnabled(true);
        etPrice.setEnabled(true);
        etUnit.setEnabled(true);
        spinnerCategory.setEnabled(true);
        btnChangeImage.setEnabled(true);
        
        // Manufacturer remains read-only
        etManufacturer.setEnabled(false);
        
        btnEdit.setVisibility(View.GONE);
        btnSave.setVisibility(View.VISIBLE);
        btnDelete.setVisibility(View.VISIBLE);
        btnCancel.setText("Cancel");
        
        Toast.makeText(this, "You can now edit the product", Toast.LENGTH_SHORT).show();
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

    private void saveProduct() {
        String name = etProductName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String unit = etUnit.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty() || unit.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price", Toast.LENGTH_SHORT).show();
            return;
        }

        // If a new image was selected, upload it first
        if (selectedImageUri != null) {
            uploadImageAndSave(name, description, price, unit, category);
        } else {
            // No new image, just update the product data
            updateProductData(name, description, price, unit, category, currentProduct.getImageUrl());
        }
    }

    private void uploadImageAndSave(String name, String description, double price, 
                                    String unit, String category) {
        Toast.makeText(this, "Processing image...", Toast.LENGTH_SHORT).show();
        
        try {
            // Convert image to Base64 like in AddProductActivity
            Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
            Bitmap resizedBitmap = resizeBitmap(originalBitmap, MAX_IMAGE_DIMENSION);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, baos);
            byte[] imageBytes = baos.toByteArray();
            String base64Image = "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.DEFAULT);
            
            // Check if image is too large
            if (base64Image.length() > 900000) {
                Toast.makeText(this, "Image too large. Please select a smaller image.", Toast.LENGTH_LONG).show();
                return;
            }
            
            updateProductData(name, description, price, unit, category, base64Image);
        } catch (IOException e) {
            Log.e("VendorProductDetails", "Failed to read image", e);
            Toast.makeText(this, "Failed to process image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void updateProductData(String name, String description, double price, 
                                   String unit, String category, String imageUrl) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("description", description);
        updates.put("price", price);
        updates.put("unit", unit);
        updates.put("category", category);
        updates.put("imageUrl", imageUrl);
        
        // If product was rejected, reset status to pending when edited
        if ("rejected".equalsIgnoreCase(currentProduct.getStatus())) {
            updates.put("status", "pending");
            updates.put("rejectionReason", null);
        }

        mDatabase.collection("products").document(productId).update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                    setViewMode();
                    loadProductData(); // Reload to show updated data
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update product: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void confirmDeleteProduct() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to delete this product? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteProduct())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteProduct() {
        // Delete product from Firestore (image is stored as Base64 in the document)
        mDatabase.collection("products").document(productId).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to delete product: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                });
    }
}
