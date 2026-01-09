package com.fariha.deshistore;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

/**
 * Helper class for uploading images to Firebase Storage
 * Works with Firebase Free (Spark) plan
 */
public class ImageUploadHelper {

    private final FirebaseStorage storage;
    private final StorageReference storageRef;
    
    // Folder structure in Firebase Storage
    private static final String PRODUCTS_FOLDER = "products/";
    private static final String CATEGORIES_FOLDER = "categories/";

    public ImageUploadHelper() {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    /**
     * Upload product image to Firebase Storage
     * @param imageUri URI of the image to upload
     * @param context Context for showing toast messages
     * @param callback Callback to receive the download URL
     */
    public void uploadProductImage(Uri imageUri, Context context, ImageUploadCallback callback) {
        uploadImage(imageUri, PRODUCTS_FOLDER, context, callback);
    }

    /**
     * Upload category image to Firebase Storage
     * @param imageUri URI of the image to upload
     * @param context Context for showing toast messages
     * @param callback Callback to receive the download URL
     */
    public void uploadCategoryImage(Uri imageUri, Context context, ImageUploadCallback callback) {
        uploadImage(imageUri, CATEGORIES_FOLDER, context, callback);
    }

    /**
     * Generic method to upload image to Firebase Storage
     */
    private void uploadImage(Uri imageUri, String folder, Context context, ImageUploadCallback callback) {
        if (imageUri == null) {
            callback.onError("No image selected");
            return;
        }

        // Generate unique filename
        String fileName = folder + UUID.randomUUID().toString() + ".jpg";
        StorageReference imageRef = storageRef.child(fileName);

        // Show upload progress
        Toast.makeText(context, "Uploading image...", Toast.LENGTH_SHORT).show();

        // Upload file
        UploadTask uploadTask = imageRef.putFile(imageUri);
        
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Get download URL
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                Toast.makeText(context, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                callback.onSuccess(downloadUrl);
            }).addOnFailureListener(e -> {
                Toast.makeText(context, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                callback.onError(e.getMessage());
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            callback.onError(e.getMessage());
        }).addOnProgressListener(snapshot -> {
            // Optional: Show upload progress
            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
            callback.onProgress((int) progress);
        });
    }

    /**
     * Delete image from Firebase Storage
     * @param imageUrl The download URL of the image to delete
     * @param callback Callback for success/failure
     */
    public void deleteImage(String imageUrl, DeleteImageCallback callback) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            callback.onError("Invalid image URL");
            return;
        }

        StorageReference imageRef = storage.getReferenceFromUrl(imageUrl);
        imageRef.delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /**
     * Callback interface for image upload
     */
    public interface ImageUploadCallback {
        void onSuccess(String downloadUrl);
        void onError(String error);
        default void onProgress(int progress) {
            // Optional: Override to show progress
        }
    }

    /**
     * Callback interface for image deletion
     */
    public interface DeleteImageCallback {
        void onSuccess();
        void onError(String error);
    }
}
