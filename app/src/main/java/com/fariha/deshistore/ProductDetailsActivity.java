package com.fariha.deshistore;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProductDetailsActivity extends AppCompatActivity {

    private Button btnBack, btnHome, btnAllProducts, btnProductCategories, btnNewlyAdded, btnMyFavourites, btnFavouriteCategories;
    private Button btnLogin, btnSignUp, btnRecommend, btnFavourite, btnSubmitReview;
    private ImageView ivProductImage;
    private TextView tvProductName, tvManufacturer, tvCategoryBadge, tvSubcategory, tvPrice;
    private TextView tvRecommendations, tvRating, tvReviewCount;
    private Spinner spinnerRating;
    private EditText etReview;
    private RecyclerView rvReviews;

    private Product product;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList;
    private boolean isFavorite = false;
    private boolean hasRecommended = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        try {
            initializeViews();
            loadProductData();
            setupRatingSpinner();
            setupReviews();
            setupClickListeners();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initializeViews() {
        // Navigation
        btnBack = findViewById(R.id.btnBack);
        btnHome = findViewById(R.id.btnHome);
        btnAllProducts = findViewById(R.id.btnAllProducts);
        btnProductCategories = findViewById(R.id.btnProductCategories);
        btnNewlyAdded = findViewById(R.id.btnNewlyAdded);
        btnMyFavourites = findViewById(R.id.btnMyFavourites);
        btnFavouriteCategories = findViewById(R.id.btnFavouriteCategories);

        // Auth
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);

        // Product info
        ivProductImage = findViewById(R.id.ivProductImage);
        tvProductName = findViewById(R.id.tvProductName);
        tvManufacturer = findViewById(R.id.tvManufacturer);
        tvCategoryBadge = findViewById(R.id.tvCategoryBadge);
        tvSubcategory = findViewById(R.id.tvSubcategory);
        tvPrice = findViewById(R.id.tvPrice);
        tvRecommendations = findViewById(R.id.tvRecommendations);
        tvRating = findViewById(R.id.tvRating);
        tvReviewCount = findViewById(R.id.tvReviewCount);

        // Actions
        btnRecommend = findViewById(R.id.btnRecommend);
        btnFavourite = findViewById(R.id.btnFavourite);

        // Review
        spinnerRating = findViewById(R.id.spinnerRating);
        etReview = findViewById(R.id.etReview);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);
        rvReviews = findViewById(R.id.rvReviews);
        
        checkLoginStatus();
    }
    
    private void checkLoginStatus() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            btnLogin.setVisibility(View.GONE);
            btnSignUp.setVisibility(View.GONE);
        }
    }

    private void loadProductData() {
        // Get product ID from intent
        String productId = getIntent().getStringExtra("product_id");
        
        // TODO: Get product data from Firebase using productId
        // For now, using sample data based on product ID
        product = getProductById(productId != null ? productId : "1");
        
        if (product == null) {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Display product info with null checks
        if (tvProductName != null) tvProductName.setText(product.getName());
        if (tvManufacturer != null && product.getManufacturer() != null) {
            tvManufacturer.setText(product.getManufacturer());
        }
        if (tvCategoryBadge != null) tvCategoryBadge.setText(product.getCategory());
        if (tvSubcategory != null) tvSubcategory.setText("Soft Drink"); // TODO: Add subcategory to Product model
        if (tvPrice != null) {
            tvPrice.setText(String.format(Locale.getDefault(), "৳ %.0f/%s", 
                    product.getPrice(), product.getUnit()));
        }
        if (tvRecommendations != null) {
            tvRecommendations.setText(product.getRecommendCount() + " Recommendations");
        }
        
        // Load product image
        if (ivProductImage != null && product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(ivProductImage);
        }
        
        // Calculate and display rating
        updateRatingDisplay();
    }
    
    private Product getProductById(String productId) {
        // Sample products with images - TODO: Replace with Firebase data
        switch (productId) {
            case "1":
                return new Product("1", "Mojo", "Beverages", 25.0, "250ml", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/mojo.jpg", "Akij Food & Beverage Ltd. (AFBL)", 16, false);
            case "2":
                return new Product("2", "MediPlus DS", "Toothpaste", 85.0, "100g", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/mediplus.jpg", "Anfords Bangladesh Ltd.", 12, false);
            case "3":
                return new Product("3", "Spa Drinking Water", "Water", 20.0, "500ml", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/spa-water.jpg", "Akij Food & Beverage Ltd. (AFBL)", 8, false);
            case "4":
                return new Product("4", "Meril Milk Soap", "Moisturizing Soap", 35.0, "75g", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/meril-soap.jpg", "Square Toiletries Ltd.", 15, false);
            case "5":
                return new Product("5", "Shezan Mango Juice", "Mango Juice", 120.0, "1L", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/shezan-juice.jpg", "Sajeeb Group", 20, false);
            case "6":
                return new Product("6", "Pran Potata Spicy", "Biscuit", 40.0, "200g", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/pran-potata.jpg", "Pran Foods Ltd.", 18, false);
            case "7":
                return new Product("7", "Ruchi BBQ Chanachur", "Snack", 30.0, "150g", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/ruchi-chanachur.jpg", "Pran Foods Ltd.", 22, false);
            case "8":
                return new Product("8", "Bashundhara Towel", "Hand Towel", 80.0, "pack", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/bashundhara-towel.jpg", "Bashundhara Paper Mills PLC", 10, false);
            case "9":
                return new Product("9", "Revive Perfect Skin", "Moisturizing Lotion", 150.0, "100ml", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/revive-lotion.jpg", "Square Toiletries Ltd.", 14, false);
            case "10":
                return new Product("10", "Jui HairCare Oil", "Hair Oil", 95.0, "200ml", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/jui-oil.jpg", "Square Toiletries Ltd.", 17, false);
            case "11":
                return new Product("11", "Radhuni Turmeric", "Powder", 55.0, "100g", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/radhuni-tumeric.jpg", "Square Food & Beverage Ltd.", 13, false);
            case "12":
                return new Product("12", "Pran Premium Ghee", "Cooking Ghee", 250.0, "500g", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/pran-ghee.jpg", "Pran Dairy Ltd.", 19, false);
            default:
                return new Product("1", "Mojo", "Beverages", 25.0, "250ml", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/mojo.jpg", "Akij Food & Beverage Ltd. (AFBL)", 16, false);
        }
    }

    private void setupRatingSpinner() {
        if (spinnerRating == null) return;
        
        String[] ratings = {"1", "2", "3", "4", "5"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_dropdown_item, ratings);
        spinnerRating.setAdapter(adapter);
        spinnerRating.setSelection(4); // Default to 5 stars
    }

    private void setupReviews() {
        reviewList = new ArrayList<>();
        
        // Sample review
        reviewList.add(new Review(
                "1",
                "1", // product ID
                "Ahmed Khan",
                "user1",
                5,
                "Great energy drink! Very refreshing.",
                new Date()
        ));

        if (rvReviews != null) {
            reviewAdapter = new ReviewAdapter(this, reviewList);
            rvReviews.setLayoutManager(new LinearLayoutManager(this));
            rvReviews.setAdapter(reviewAdapter);
        }
    }

    private void updateRatingDisplay() {
        if (tvRating == null || tvReviewCount == null || reviewList == null) {
            return;
        }
        
        if (reviewList.isEmpty()) {
            tvRating.setText("0.0");
            tvReviewCount.setText("(0 reviews)");
        } else {
            double avgRating = calculateAverageRating();
            tvRating.setText(String.format(Locale.getDefault(), "%.1f", avgRating));
            tvReviewCount.setText("(" + reviewList.size() + " reviews)");
        }
    }

    private double calculateAverageRating() {
        if (reviewList.isEmpty()) return 0.0;
        
        int total = 0;
        for (Review review : reviewList) {
            total += review.getRating();
        }
        return (double) total / reviewList.size();
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Navigation buttons
        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        btnAllProducts.setOnClickListener(v -> {
            startActivity(new Intent(this, AllProductsActivity.class));
            finish();
        });

        btnProductCategories.setOnClickListener(v -> {
            startActivity(new Intent(this, ProductCategoriesActivity.class));
            finish();
        });

        btnNewlyAdded.setOnClickListener(v -> {
            startActivity(new Intent(this, NewlyAddedActivity.class));
            finish();
        });

        btnMyFavourites.setOnClickListener(v -> {
            startActivity(new Intent(this, MyFavouritesActivity.class));
            finish();
        });

        btnFavouriteCategories.setOnClickListener(v -> {
            startActivity(new Intent(this, FavouriteCategoriesActivity.class));
            finish();
        });

        // Auth buttons
        btnLogin.setOnClickListener(v -> {
            Toast.makeText(this, "Login", Toast.LENGTH_SHORT).show();
        });

        btnSignUp.setOnClickListener(v -> {
            Toast.makeText(this, "Sign Up", Toast.LENGTH_SHORT).show();
        });

        // Recommend button - toggle functionality
        btnRecommend.setOnClickListener(v -> {
            if (!hasRecommended) {
                // Recommend the product
                product.incrementRecommendCount();
                hasRecommended = true;
                if (tvRecommendations != null) {
                    tvRecommendations.setText(product.getRecommendCount() + " Recommendations");
                }
                
                // Change button to active state
                btnRecommend.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green)));
                btnRecommend.setTextColor(ContextCompat.getColor(this, R.color.white));
                
                Toast.makeText(this, "Product recommended!", Toast.LENGTH_SHORT).show();
            } else {
                // Unrecommend the product
                if (product.getRecommendCount() > 0) {
                    product.setRecommendCount(product.getRecommendCount() - 1);
                }
                hasRecommended = false;
                if (tvRecommendations != null) {
                    tvRecommendations.setText(product.getRecommendCount() + " Recommendations");
                }
                
                // Change button back to inactive state
                btnRecommend.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
                btnRecommend.setTextColor(ContextCompat.getColor(this, R.color.green));
                
                Toast.makeText(this, "Recommendation removed", Toast.LENGTH_SHORT).show();
            }
            // TODO: Update in database
        });

        // Favourite button
        btnFavourite.setOnClickListener(v -> {
            isFavorite = !isFavorite;
            updateFavoriteButton();
            
            if (isFavorite) {
                Toast.makeText(this, "Added to favourites", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Removed from favourites", Toast.LENGTH_SHORT).show();
            }
            // TODO: Update in database and SharedPreferences
        });

        // Submit Review button
        btnSubmitReview.setOnClickListener(v -> submitReview());
    }

    private void updateFavoriteButton() {
        if (isFavorite) {
            btnFavourite.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red)));
            btnFavourite.setTextColor(ContextCompat.getColor(this, R.color.white));
            btnFavourite.setText("♥ Favourite");
        } else {
            btnFavourite.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
            btnFavourite.setTextColor(ContextCompat.getColor(this, R.color.red));
            btnFavourite.setText("♡ Favourite");
        }
    }

    private void submitReview() {
        String reviewText = etReview.getText().toString().trim();
        
        if (reviewText.isEmpty()) {
            Toast.makeText(this, "Please write a review", Toast.LENGTH_SHORT).show();
            return;
        }

        int rating = Integer.parseInt(spinnerRating.getSelectedItem().toString());
        
        // Create new review
        Review newReview = new Review(
                String.valueOf(System.currentTimeMillis()),
                product.getId(),
                "Current User", // TODO: Get from logged in user
                "currentUserId", // TODO: Get from logged in user
                rating,
                reviewText,
                new Date()
        );

        // Add to list and update UI
        reviewList.add(0, newReview);
        reviewAdapter.notifyItemInserted(0);
        updateRatingDisplay();

        // Clear input
        etReview.setText("");
        spinnerRating.setSelection(4);

        Toast.makeText(this, "Review submitted successfully!", Toast.LENGTH_SHORT).show();
        
        // TODO: Save to database
    }
}
