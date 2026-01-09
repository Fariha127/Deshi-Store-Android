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

public class ProductCategoriesActivity extends AppCompatActivity {

    private RecyclerView rvCategories;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private Button btnHome, btnAllProducts, btnProductCategories, btnNewlyAdded, btnMyFavourites, btnFavouriteCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_categories);

        initializeViews();
        setupCategoryList();
        setupRecyclerView();
        setupClickListeners();
    }

    private void initializeViews() {
        rvCategories = findViewById(R.id.rvProducts);
        btnHome = findViewById(R.id.btnHome);
        btnAllProducts = findViewById(R.id.btnAllProducts);
        btnProductCategories = findViewById(R.id.btnProductCategories);
        btnNewlyAdded = findViewById(R.id.btnNewlyAdded);
        btnMyFavourites = findViewById(R.id.btnMyFavourites);
        btnFavouriteCategories = findViewById(R.id.btnFavouriteCategories);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Product Categories");
        }
    }

    private void setupCategoryList() {
        categoryList = new ArrayList<>();
        
        categoryList.add(new Category("1", "Beverages", "Energy Drinks, Soft Drinks, Juices", ""));
        categoryList.add(new Category("2", "Hair Care", "Hair Shampoo, Hair Oil", ""));
        categoryList.add(new Category("3", "Oral Care", "Toothpaste, Mouthwash", ""));
        categoryList.add(new Category("4", "Snacks", "Biscuits, Chips, Chanachur", ""));
        categoryList.add(new Category("5", "Food & Grocery", "Rice, Spices, Ready Mixes, Sauces, Mustard Oils, Rice Bran Oils, ...", ""));
        categoryList.add(new Category("6", "Home Care", "Cleaners, Handwash, Detergent, Hand Towel, Mosquito repellents, ...", ""));
        categoryList.add(new Category("7", "Skin Care", "Body Soap, Moisterizer, Body Lotion, Face Cleanser, Lip Care", ""));
        categoryList.add(new Category("8", "Baby Care", "Diapers, Baby Care products", ""));
        categoryList.add(new Category("9", "Dairy Products", "Milk, Milk Powder, Ghee, Yoghurt, Condensed Milk", ""));
    }

    private void setupRecyclerView() {
        categoryAdapter = new CategoryAdapter(this, categoryList);
        rvCategories.setLayoutManager(new LinearLayoutManager(this));
        rvCategories.setAdapter(categoryAdapter);
        rvCategories.setHasFixedSize(false);
        
        // Calculate and set minimum height
        int itemHeight = (int) (140 * getResources().getDisplayMetrics().density);
        int extraPadding = (int) (100 * getResources().getDisplayMetrics().density);
        int totalHeight = (categoryList.size() * itemHeight) + extraPadding;
        rvCategories.setMinimumHeight(totalHeight);
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
            // Already on Product Categories
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
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
