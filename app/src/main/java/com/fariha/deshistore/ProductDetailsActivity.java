package com.fariha.deshistore;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProductDetailsActivity extends AppCompatActivity {

    private Button btnBack, btnHome, btnProductCategories, btnNewlyAdded, btnMyFavourites, btnFavouriteCategories;
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

        initializeViews();
        loadProductData();
        setupRatingSpinner();
        setupReviews();
        setupClickListeners();
    }

    private void initializeViews() {
        // Navigation
        btnBack = findViewById(R.id.btnBack);
        btnHome = findViewById(R.id.btnHome);
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
    }

    private void loadProductData() {
        // TODO: Get product data from Intent or Firebase
        // For now, using sample data
        product = new Product(
                "1",
                "Mojo",
                "Beverages",
                25.0,
                "250ml",
                "",
                "Akij Food & Beverage Ltd. (AFBL)",
                16,
                false
        );

        // Display product info
        tvProductName.setText(product.getName());
        tvManufacturer.setText(product.getManufacturer());
        tvCategoryBadge.setText(product.getCategory());
        tvSubcategory.setText("Soft Drink"); // TODO: Add subcategory to Product model
        tvPrice.setText(String.format(Locale.getDefault(), "৳ %.0f/%s", 
                product.getPrice(), product.getUnit()));
        tvRecommendations.setText(product.getRecommendCount() + " Recommendations");
        
        // Calculate and display rating
        updateRatingDisplay();
    }

    private void setupRatingSpinner() {
        String[] ratings = {"1", "2", "3", "4", "5"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, ratings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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

        reviewAdapter = new ReviewAdapter(this, reviewList);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setAdapter(reviewAdapter);
    }

    private void updateRatingDisplay() {
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
            // Navigate to Home
            finish();
        });

        btnProductCategories.setOnClickListener(v -> {
            Toast.makeText(this, "Product Categories", Toast.LENGTH_SHORT).show();
        });

        btnNewlyAdded.setOnClickListener(v -> {
            Toast.makeText(this, "Newly Added", Toast.LENGTH_SHORT).show();
        });

        btnMyFavourites.setOnClickListener(v -> {
            Toast.makeText(this, "My Favourites", Toast.LENGTH_SHORT).show();
        });

        btnFavouriteCategories.setOnClickListener(v -> {
            Toast.makeText(this, "Favourite Categories", Toast.LENGTH_SHORT).show();
        });

        // Auth buttons
        btnLogin.setOnClickListener(v -> {
            Toast.makeText(this, "Login", Toast.LENGTH_SHORT).show();
        });

        btnSignUp.setOnClickListener(v -> {
            Toast.makeText(this, "Sign Up", Toast.LENGTH_SHORT).show();
        });

        // Recommend button
        btnRecommend.setOnClickListener(v -> {
            if (!hasRecommended) {
                product.incrementRecommendCount();
                hasRecommended = true;
                tvRecommendations.setText(product.getRecommendCount() + " Recommendations");
                
                // Change button to active state
                btnRecommend.setBackgroundResource(R.drawable.button_recommend_active);
                btnRecommend.setTextColor(getResources().getColor(android.R.color.white));
                
                Toast.makeText(this, "Product recommended!", Toast.LENGTH_SHORT).show();
                // TODO: Update in database
            } else {
                Toast.makeText(this, "You have already recommended this product", Toast.LENGTH_SHORT).show();
            }
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
            btnFavourite.setBackgroundResource(R.drawable.button_favourite_active);
            btnFavourite.setTextColor(getResources().getColor(android.R.color.white));
            btnFavourite.setText("♥ Favourite");
        } else {
            btnFavourite.setBackgroundResource(R.drawable.button_favourite_border);
            btnFavourite.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
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
