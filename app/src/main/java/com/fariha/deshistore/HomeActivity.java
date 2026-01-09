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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
        
        // Hide login/signup buttons if user is logged in
        checkLoginStatus();
    }
    
    private void checkLoginStatus() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // User is logged in, hide auth buttons
            btnLogin.setVisibility(View.GONE);
            btnSignUp.setVisibility(View.GONE);
        }
    }

    private void setupProductList() {
        productList = new ArrayList<>();
        
        // Sample data with actual product images from images folder
        // Images loaded from GitHub repository
        productList.add(new Product(
                "1",
                "Mojo",
                "Soft Drink",
                25.0,
                "250ml",
                "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/mojo.jpg",
                "Akij Food & Beverage Ltd. (AFBL)",
                0,
                false
        ));

        productList.add(new Product(
                "2",
                "MediPlus DS",
                "Toothpaste",
                85.0,
                "100g",
                "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/mediplus.jpg",
                "Anfords Bangladesh Ltd.",
                0,
                false
        ));

        productList.add(new Product(
                "3",
                "Spa Drinking Water",
                "Water",
                20.0,
                "500ml",
                "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/spa-water.jpg",
                "Akij Food & Beverage Ltd. (AFBL)",
                0,
                false
        ));

        productList.add(new Product(
                "4",
                "Meril Milk Soap",
                "Moisturizing Soap",
                35.0,
                "75g",
                "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/meril-soap.jpg",
                "Square Toiletries Ltd.",
                0,
                false
        ));

        productList.add(new Product(
                "5",
                "Shezan Mango Juice",
                "Mango Juice",
                120.0,
                "1L",
                "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/shezan-juice.jpg",
                "Sajeeb Group",
                0,
                false
        ));

        productList.add(new Product(
                "6",
                "Pran Potata Spicy",
                "Biscuit",
                40.0,
                "200g",
                "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/pran-potata.jpg",
                "Pran Foods Ltd.",
                0,
                false
        ));

        productList.add(new Product(
                "7",
                "Ruchi BBQ Chanachur",
                "Snack",
                30.0,
                "150g",
                "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/ruchi-chanachur.jpg",
                "Pran Foods Ltd.",
                0,
                false
        ));

        productList.add(new Product(
                "8",
                "Bashundhara Towel",
                "Hand Towel",
                80.0,
                "pack",
                "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/bashundhara-towel.jpg",
                "Bashundhara Paper Mills PLC",
                0,
                false
        ));

        productList.add(new Product(
                "9",
                "Revive Perfect Skin",
                "Moisturizing Lotion",
                150.0,
                "100ml",
                "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/revive-lotion.jpg",
                "Square Toiletries Ltd.",
                0,
                false
        ));

        productList.add(new Product(
                "10",
                "Jui HairCare Oil",
                "Hair Oil",
                95.0,
                "200ml",
                "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/jui-oil.jpg",
                "Square Toiletries Ltd.",
                0,
                false
        ));

        productList.add(new Product(
                "11",
                "Radhuni Turmeric",
                "Powder",
                55.0,
                "100g",
                "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/radhuni-tumeric.jpg",
                "Square Food & Beverage Ltd.",
                0,
                false
        ));

        productList.add(new Product(
                "12",
                "Pran Premium Ghee",
                "Cooking Ghee",
                250.0,
                "500g",
                "https://raw.githubusercontent.com/Fariha127/Deshi-Store-Android/main/images/pran-ghee.jpg",
                "Pran Dairy Ltd.",
                0,
                false
        ));
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(this, productList);
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvProducts.setAdapter(productAdapter);
        rvProducts.setHasFixedSize(false);
        
        // Calculate and set minimum height based on item count
        // Approximate item height: 400dp per item + extra padding
        int itemHeight = (int) (400 * getResources().getDisplayMetrics().density);
        int rows = (int) Math.ceil(productList.size() / 2.0);
        int extraPadding = (int) (100 * getResources().getDisplayMetrics().density);
        int totalHeight = (rows * itemHeight) + extraPadding;
        rvProducts.setMinimumHeight(totalHeight);
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
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        });

        btnSignUp.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, UserSignUpActivity.class));
        });

        // View All
        tvViewAll.setOnClickListener(v -> {
            // View all products
            startActivity(new Intent(this, AllProductsActivity.class));
        });
    }
}
