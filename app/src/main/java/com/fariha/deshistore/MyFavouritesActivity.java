package com.fariha.deshistore;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyFavouritesActivity extends AppCompatActivity {

    private RecyclerView rvFavourites;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private List<Product> favoriteProducts;
    private Button btnHome, btnAllProducts, btnProductCategories, btnNewlyAdded, btnMyFavourites, btnFavouriteCategories;
    private Button btnLogin, btnSignUp;
    private static final String PREFS_NAME = "FavoritesPrefs";
    private static final String FAVORITES_KEY = "favorites";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favourites);

        initializeViews();
        setupProductList();
        setupRecyclerView();
        setupClickListeners();
    }

    private void initializeViews() {
        rvFavourites = findViewById(R.id.rvProducts);
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
            getSupportActionBar().setTitle("My Favourites");
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
            // Already on My Favourites
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

    private void setupProductList() {
        // Initialize all products list with images (same as HomeActivity)
        productList = new ArrayList<>();
        
        productList.add(new Product("1", "Mojo", "Soft Drink", 25.0, "250ml", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/mojo.jpg", "Akij Food & Beverage Ltd.", 0, false));
        productList.add(new Product("2", "MediPlus DS", "Toothpaste", 85.0, "100g", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/mediplus.jpg", "Anfords Bangladesh Ltd.", 0, false));
        productList.add(new Product("3", "Spa Drinking Water", "Water", 20.0, "500ml", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/spa-water.jpg", "Akij Food & Beverage Ltd.", 0, false));
        productList.add(new Product("4", "Meril Milk Soap", "Moisturizing Soap", 35.0, "75g", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/meril-soap.jpg", "Square Toiletries Ltd.", 0, false));
        productList.add(new Product("5", "Shezan Mango Juice", "Mango Juice", 120.0, "1L", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/shezan-juice.jpg", "Sajeeb Group", 0, false));
        productList.add(new Product("6", "Pran Potata Spicy", "Biscuit", 40.0, "200g", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/pran-potata.jpg", "Pran Foods Ltd.", 0, false));
        productList.add(new Product("7", "Ruchi BBQ Chanachur", "Snack", 30.0, "150g", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/ruchi-chanachur.jpg", "Pran Foods Ltd.", 0, false));
        productList.add(new Product("8", "Bashundhara Towel", "Hand Towel", 80.0, "pack", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/bashundhara-towel.jpg", "Bashundhara Paper Mills PLC", 0, false));
        productList.add(new Product("9", "Revive Perfect Skin", "Moisturizing Lotion", 150.0, "100ml", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/revive-lotion.jpg", "Square Toiletries Ltd.", 0, false));
        productList.add(new Product("10", "Jui HairCare Oil", "Hair Oil", 95.0, "200ml", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/jui-oil.jpg", "Square Toiletries Ltd.", 0, false));
        productList.add(new Product("11", "Radhuni Turmeric", "Powder", 55.0, "100g", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/radhuni-tumeric.jpg", "Square Food & Beverage Ltd.", 0, false));
        productList.add(new Product("12", "Pran Premium Ghee", "Cooking Ghee", 250.0, "500g", "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/pran-ghee.jpg", "Pran Dairy Ltd.", 0, false));
        
        // Filter only favorite products
        favoriteProducts = new ArrayList<>();
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> favorites = prefs.getStringSet(FAVORITES_KEY, new HashSet<>());
        
        for (Product product : productList) {
            if (favorites.contains(product.getId())) {
                favoriteProducts.add(product);
            }
        }
        
        if (favoriteProducts.isEmpty()) {
            Toast.makeText(this, "No favorite products yet", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(this, favoriteProducts);
        rvFavourites.setLayoutManager(new GridLayoutManager(this, 2));
        rvFavourites.setAdapter(productAdapter);
        rvFavourites.setHasFixedSize(false);
        
        // Calculate and set minimum height based on item count
        // Approximate item height: 400dp per item + extra padding
        int itemHeight = (int) (400 * getResources().getDisplayMetrics().density);
        int rows = (int) Math.ceil(favoriteProducts.size() / 2.0);
        int extraPadding = (int) (100 * getResources().getDisplayMetrics().density);
        int totalHeight = (rows * itemHeight) + extraPadding;
        rvFavourites.setMinimumHeight(totalHeight);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh favorites list when returning to this activity
        setupProductList();
        if (productAdapter != null) {
            productAdapter.updateProductList(favoriteProducts);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
