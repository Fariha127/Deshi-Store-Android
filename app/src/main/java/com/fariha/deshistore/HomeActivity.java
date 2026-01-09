package com.fariha.deshistore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private EditText etSearch;
    private Button btnHome, btnAllProducts, btnProductCategories, btnNewlyAdded, btnMyFavourites, btnFavouriteCategories;
    private Button btnLogin, btnSignUp;
    private TextView tvViewAll;
    private RecyclerView rvProducts;

    private ProductAdapter productAdapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeViews();
        setupProductList();
        setupRecyclerView();
        setupClickListeners();
    }

    private void initializeViews() {
        // Search
        etSearch = findViewById(R.id.etSearch);

        // Navigation buttons
        btnHome = findViewById(R.id.btnHome);
        btnAllProducts = findViewById(R.id.btnAllProducts);
        btnProductCategories = findViewById(R.id.btnProductCategories);
        btnNewlyAdded = findViewById(R.id.btnNewlyAdded);
        btnMyFavourites = findViewById(R.id.btnMyFavourites);
        btnFavouriteCategories = findViewById(R.id.btnFavouriteCategories);

        // Auth buttons
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);

        // Other views
        tvViewAll = findViewById(R.id.tvViewAll);
        rvProducts = findViewById(R.id.rvProducts);
    }

    private void setupProductList() {
        productList = new ArrayList<>();
        
        // Sample data - Replace with actual data from Firebase/Database
        productList.add(new Product(
                "1",
                "Mojo",
                "Soft Drink",
                25.0,
                "250ml",
                "",
                "Sample Manufacturer",
                0,
                false
        ));

        productList.add(new Product(
                "2",
                "MediPlus DS",
                "Toothpaste",
                85.0,
                "100g",
                "",
                "Sample Manufacturer",
                0,
                false
        ));

        productList.add(new Product(
                "3",
                "Spa Drinking Water",
                "Water",
                20.0,
                "500ml",
                "",
                "Sample Manufacturer",
                0,
                false
        ));

        productList.add(new Product(
                "4",
                "Meril Milk Soap",
                "Moisturizing Soap",
                35.0,
                "75g",
                "",
                "Sample Manufacturer",
                0,
                false
        ));

        productList.add(new Product(
                "5",
                "Shezan Mango Juice",
                "Mango Juice",
                120.0,
                "1L",
                "",
                "Sample Manufacturer",
                0,
                false
        ));

        productList.add(new Product(
                "6",
                "Pran Potata Spicy",
                "Biscuit",
                40.0,
                "200g",
                "",
                "Sample Manufacturer",
                0,
                false
        ));

        productList.add(new Product(
                "7",
                "Ruchi BBQ Chanachur",
                "Snack",
                30.0,
                "150g",
                "",
                "Sample Manufacturer",
                0,
                false
        ));

        productList.add(new Product(
                "8",
                "Bashundhara Towel",
                "Hand Towel",
                80.0,
                "pack",
                "",
                "Sample Manufacturer",
                0,
                false
        ));

        productList.add(new Product(
                "9",
                "Revive Perfect Skin",
                "Moisturizing Lotion",
                150.0,
                "100ml",
                "",
                "Sample Manufacturer",
                0,
                false
        ));

        productList.add(new Product(
                "10",
                "Jui HairCare Oil",
                "Hair Oil",
                95.0,
                "200ml",
                "",
                "Sample Manufacturer",
                0,
                false
        ));

        productList.add(new Product(
                "11",
                "Radhuni Turmeric",
                "Powder",
                55.0,
                "100g",
                "",
                "Sample Manufacturer",
                0,
                false
        ));

        productList.add(new Product(
                "12",
                "Pran Premium Ghee",
                "Cooking Ghee",
                250.0,
                "500g",
                "",
                "Sample Manufacturer",
                0,
                false
        ));
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(this, productList);
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvProducts.setAdapter(productAdapter);
    }

    private void setupClickListeners() {
        // Navigation buttons
        btnHome.setOnClickListener(v -> {
            // Already on home
            Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
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
            // Navigate to Login
            Toast.makeText(this, "Login", Toast.LENGTH_SHORT).show();
            // Intent to LoginActivity
        });

        btnSignUp.setOnClickListener(v -> {
            // Navigate to Sign Up
            Toast.makeText(this, "Sign Up", Toast.LENGTH_SHORT).show();
            // Intent to SignUpActivity
        });

        // View All
        tvViewAll.setOnClickListener(v -> {
            // View all products
            startActivity(new Intent(this, AllProductsActivity.class));
        });
    }
}
