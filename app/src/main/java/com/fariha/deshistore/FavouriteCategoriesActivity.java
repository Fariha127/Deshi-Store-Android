package com.fariha.deshistore;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavouriteCategoriesActivity extends AppCompatActivity {

    private RecyclerView rvFavouriteCategories;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private List<Category> favoriteCategories;
    private Button btnHome, btnAllProducts, btnProductCategories, btnNewlyAdded, btnMyFavourites, btnFavouriteCategories;
    private static final String PREFS_NAME = "FavoritesPrefs";
    private static final String FAVORITE_CATEGORIES_KEY = "favorite_categories";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_categories);

        initializeViews();
        setupCategoryList();
        setupClickListeners();
    }

    private void initializeViews() {
        rvFavouriteCategories = findViewById(R.id.rvProducts);
        btnHome = findViewById(R.id.btnHome);
        btnAllProducts = findViewById(R.id.btnAllProducts);
        btnProductCategories = findViewById(R.id.btnProductCategories);
        btnNewlyAdded = findViewById(R.id.btnNewlyAdded);
        btnMyFavourites = findViewById(R.id.btnMyFavourites);
        btnFavouriteCategories = findViewById(R.id.btnFavouriteCategories);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Favourite Categories");
        }
    }

    private void setupClickListeners() {
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
            // Already on Favourite Categories
        });
    }

    private void setupCategoryList() {
        categoryList = new ArrayList<>();
        
        // Initialize all 9 categories
        categoryList.add(new Category("1", "Beverages", "Refresh yourself with our beverage collection", ""));
        categoryList.add(new Category("2", "Hair Care", "Premium hair care products for healthy hair", ""));
        categoryList.add(new Category("3", "Oral Care", "Complete oral hygiene solutions", ""));
        categoryList.add(new Category("4", "Snacks", "Delicious snacks for every occasion", ""));
        categoryList.add(new Category("5", "Food & Grocery", "Daily essentials and grocery items", ""));
        categoryList.add(new Category("6", "Home Care", "Keep your home clean and fresh", ""));
        categoryList.add(new Category("7", "Skin Care", "Nourish your skin naturally", ""));
        categoryList.add(new Category("8", "Baby Care", "Gentle care for your little ones", ""));
        categoryList.add(new Category("9", "Dairy Products", "Fresh dairy products daily", ""));
        
        // Filter favorite categories
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> favoriteIds = prefs.getStringSet(FAVORITE_CATEGORIES_KEY, new HashSet<>());
        
        favoriteCategories = new ArrayList<>();
        for (Category category : categoryList) {
            if (favoriteIds.contains(category.getId())) {
                favoriteCategories.add(category);
            }
        }
        
        if (favoriteCategories.isEmpty()) {
            Toast.makeText(this, "No favourite categories yet", Toast.LENGTH_SHORT).show();
        }
        
        setupRecyclerView();
    }
    
    private void setupRecyclerView() {
        categoryAdapter = new CategoryAdapter(this, favoriteCategories);
        rvFavouriteCategories.setLayoutManager(new LinearLayoutManager(this));
        rvFavouriteCategories.setAdapter(categoryAdapter);
        rvFavouriteCategories.setNestedScrollingEnabled(false);
        
        // Calculate dynamic height for categories (140dp per category + extra padding)
        int itemCount = favoriteCategories.size();
        int itemHeight = (int) (140 * getResources().getDisplayMetrics().density); // 140dp to pixels
        int extraPadding = (int) (100 * getResources().getDisplayMetrics().density);
        int totalHeight = (itemHeight * itemCount) + extraPadding;
        
        rvFavouriteCategories.getLayoutParams().height = totalHeight;
        rvFavouriteCategories.requestLayout();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list when returning to this activity
        setupCategoryList();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
