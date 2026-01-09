package com.fariha.deshistore;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

/**
 * Simple example: Pick image from gallery and upload to Firebase Storage
 * Works on Android phone - user picks image from their device
 */
public class ImagePickerExample extends AppCompatActivity {

    private ImageView ivSelectedImage;
    private Button btnPickImage, btnUploadImage;
    private Uri selectedImageUri;
    private ImageUploadHelper imageUploadHelper;

    // Image picker launcher
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    
                    // Show preview
                    Glide.with(this)
                            .load(selectedImageUri)
                            .into(ivSelectedImage);
                    
                    btnUploadImage.setEnabled(true);
                    Toast.makeText(this, "Image selected! Now click Upload", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_image_picker_example);

        // Initialize views (you'd do this with your layout)
        ivSelectedImage = findViewById(R.id.ivSelectedImage);
        btnPickImage = findViewById(R.id.btnPickImage);
        btnUploadImage = findViewById(R.id.btnUploadImage);

        imageUploadHelper = new ImageUploadHelper();
        btnUploadImage.setEnabled(false);

        // Pick image button
        btnPickImage.setOnClickListener(v -> pickImageFromGallery());

        // Upload image button
        btnUploadImage.setOnClickListener(v -> uploadImageToFirebase());
    }

    /**
     * Step 1: Pick image from device gallery
     */
    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    /**
     * Step 2: Upload selected image to Firebase Storage
     */
    private void uploadImageToFirebase() {
        if (selectedImageUri == null) {
            Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show();
            return;
        }

        btnUploadImage.setEnabled(false);
        btnUploadImage.setText("Uploading...");

        // Upload to Firebase Storage
        imageUploadHelper.uploadProductImage(selectedImageUri, this, 
            new ImageUploadHelper.ImageUploadCallback() {
                @Override
                public void onSuccess(String downloadUrl) {
                    // SUCCESS! You now have the Firebase URL
                    Toast.makeText(ImagePickerExample.this, 
                        "Upload successful!", Toast.LENGTH_LONG).show();
                    
                    // Use this URL in your Product object
                    // product.setImageUrl(downloadUrl);
                    
                    // Or show the URL to copy
                    showUploadedUrl(downloadUrl);
                    
                    btnUploadImage.setEnabled(true);
                    btnUploadImage.setText("Upload Image");
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(ImagePickerExample.this, 
                        "Upload failed: " + error, Toast.LENGTH_SHORT).show();
                    btnUploadImage.setEnabled(true);
                    btnUploadImage.setText("Upload Image");
                }

                @Override
                public void onProgress(int progress) {
                    btnUploadImage.setText("Uploading... " + progress + "%");
                }
            });
    }

    private void showUploadedUrl(String url) {
        // Show the URL in a dialog or copy to clipboard
        Toast.makeText(this, "Image URL: " + url, Toast.LENGTH_LONG).show();
        // You can now use this URL in your Product/Category objects!
    }
}
