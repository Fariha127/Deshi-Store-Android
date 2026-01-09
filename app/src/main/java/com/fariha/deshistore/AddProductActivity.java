package com.fariha.deshistore;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Example Activity showing how to add products with images to Firebase
 * This works with Firebase Free (Spark) plan
 */
public class AddProductActivity extends AppCompatActivity {

    private EditText etProductName, etCategory, etPrice, etUnit, etManufacturer;
    private ImageView ivPreview;
    private Button btnSelectImage, btnUpload;
    private ProgressBar progressBar;
    
    private Uri selectedImageUri;
    private ImageUploadHelper imageUploadHelper;
    private DatabaseReference productsRef;
    
    // Activity result launcher for image selection
    private final ActivityResultLauncher<Intent> imagePickerLauncher = 
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                selectedImageUri = result.getData().getData();
                // Show preview
                Glide.with(this)
                        .load(selectedImageUri)
                        .into(ivPreview);
                btnUpload.setEnabled(true);
            }
        });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        initializeViews();
        setupFirebase();
        setupClickListeners();
    }

    private void initializeViews() {
        etProductName = findViewById(R.id.etProductName);
        etCategory = findViewById(R.id.etCategory);
        etPrice = findViewById(R.id.etPrice);
        etUnit = findViewById(R.id.etUnit);
        etManufacturer = findViewById(R.id.etManufacturer);
        ivPreview = findViewById(R.id.ivPreview);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnUpload = findViewById(R.id.btnUpload);
        progressBar = findViewById(R.id.progressBar);
        
        btnUpload.setEnabled(false);
        progressBar.setMax(100);
    }

    private void setupFirebase() {
        imageUploadHelper = new ImageUploadHelper();
        productsRef = FirebaseDatabase.getInstance().getReference("products");
    }

    private void setupClickListeners() {
        btnSelectImage.setOnClickListener(v -> openImagePicker());
        btnUpload.setOnClickListener(v -> uploadProductWithImage());
    }

    /**
     * Open image picker to select image from gallery
     */
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    /**
     * Upload product with image to Firebase
     */
    private void uploadProductWithImage() {
        // Validate inputs
        String name = etProductName.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String unit = etUnit.getText().toString().trim();
        String manufacturer = etManufacturer.getText().toString().trim();

        if (name.isEmpty() || category.isEmpty() || priceStr.isEmpty()) {
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

        // Disable button during upload
        btnUpload.setEnabled(false);
        progressBar.setProgress(0);

        // Upload image first
        imageUploadHelper.uploadProductImage(selectedImageUri, this, new ImageUploadHelper.ImageUploadCallback() {
            @Override
            public void onSuccess(String downloadUrl) {
                // Create product with image URL
                String productId = productsRef.push().getKey();
                Product product = new Product(
                    productId,
                    name,
                    category,
                    price,
                    unit,
                    downloadUrl,  // Firebase Storage URL
                    manufacturer,
                    0,
                    false
                );

                // Save to Firebase Database
                if (productId != null) {
                    productsRef.child(productId).setValue(product)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(AddProductActivity.this, 
                                "Product added successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(AddProductActivity.this, 
                                "Failed to save product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            btnUpload.setEnabled(true);
                        });
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AddProductActivity.this, 
                    "Upload failed: " + error, Toast.LENGTH_SHORT).show();
                btnUpload.setEnabled(true);
            }

            @Override
            public void onProgress(int progress) {
                progressBar.setProgress(progress);
            }
        });
    }
}
