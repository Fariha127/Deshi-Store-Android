package com.fariha.deshistore;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductCategoriesActivity extends AppCompatActivity {

    private RecyclerView rvCategories;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private Button btnHome, btnAllProducts, btnProductCategories, btnNewlyAdded, btnMyFavourites, btnFavouriteCategories;
    private Button btnLogin, btnSignUp;

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
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Product Categories");
        }
        
        checkLoginStatus();
    }
    
    private void checkLoginStatus() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            btnLogin.setVisibility(View.GONE);
            btnSignUp.setVisibility(View.GONE);
        }
    }

    private void setupCategoryList() {
        categoryList = new ArrayList<>();
        
        // Sample categories with actual category images from images folder
        // Images loaded from GitHub repository
        categoryList.add(new Category("1", "Beverages", "Energy Drinks, Soft Drinks, Juices", 
            "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/beverages-category.jpg"));
        categoryList.add(new Category("2", "Hair Care", "Hair Shampoo, Hair Oil", 
            "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/haircare-category.jpg"));
        categoryList.add(new Category("3", "Oral Care", "Toothpaste, Mouthwash", 
            "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/oralcare-category.jpg"));
        categoryList.add(new Category("4", "Snacks", "Biscuits, Chips, Chanachur", 
            "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/snacks-category.jpg"));
        categoryList.add(new Category("5", "Food & Grocery", "Rice, Spices, Ready Mixes, Sauces, Mustard Oils, Rice Bran Oils, ...", 
            "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/foodgrocery-category.jpg"));
        categoryList.add(new Category("6", "Home Care", "Cleaners, Handwash, Detergent, Hand Towel, Mosquito repellents, ...", 
            "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/homecare-category.jpg"));
        categoryList.add(new Category("7", "Skin Care", "Body Soap, Moisterizer, Body Lotion, Face Cleanser, Lip Care", 
            "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/skincare-category.jpg"));
        categoryList.add(new Category("8", "Baby Care", "Diapers, Baby Care products", 
            "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/babycare-category.jpg"));
        categoryList.add(new Category("9", "Dairy Products", "Milk, Milk Powder, Ghee, Yoghurt, Condensed Milk", 
            "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/dairy-category.jpg"));
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

        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });

        btnSignUp.setOnClickListener(v -> {
            startActivity(new Intent(this, UserSignUpActivity.class));
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
