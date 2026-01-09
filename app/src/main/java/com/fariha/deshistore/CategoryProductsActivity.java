package com.fariha.deshistore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CategoryProductsActivity extends AppCompatActivity {

    private RecyclerView rvProducts;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private String categoryId;
    private String categoryName;
    private TextView tvCategoryTitle;
    private Button btnHome, btnAllProducts, btnProductCategories, btnNewlyAdded, btnMyFavourites, btnFavouriteCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_products);

        categoryId = getIntent().getStringExtra("category_id");
        categoryName = getIntent().getStringExtra("category_name");

        initializeViews();
        setupProductList();
        setupRecyclerView();
        setupClickListeners();
    }

    private void initializeViews() {
        rvProducts = findViewById(R.id.rvProducts);
        tvCategoryTitle = findViewById(R.id.tvCategoryTitle);
        btnHome = findViewById(R.id.btnHome);
        btnAllProducts = findViewById(R.id.btnAllProducts);
        btnProductCategories = findViewById(R.id.btnProductCategories);
        btnNewlyAdded = findViewById(R.id.btnNewlyAdded);
        btnMyFavourites = findViewById(R.id.btnMyFavourites);
        btnFavouriteCategories = findViewById(R.id.btnFavouriteCategories);
        
        // Show category title
        tvCategoryTitle.setText(categoryName);
        tvCategoryTitle.setVisibility(View.VISIBLE);
        
        // Highlight Product Categories button with white text
        btnProductCategories.setBackgroundResource(R.drawable.button_green);
        btnProductCategories.setTextColor(getResources().getColor(android.R.color.white));
        
        // Set other buttons to white background with gray text
        btnHome.setBackgroundResource(R.drawable.button_white);
        btnHome.setTextColor(getResources().getColor(android.R.color.darker_gray));
        
        btnAllProducts.setBackgroundResource(R.drawable.button_white);
        btnAllProducts.setTextColor(getResources().getColor(android.R.color.darker_gray));
        
        btnNewlyAdded.setBackgroundResource(R.drawable.button_white);
        btnNewlyAdded.setTextColor(getResources().getColor(android.R.color.darker_gray));
        
        btnMyFavourites.setBackgroundResource(R.drawable.button_white);
        btnMyFavourites.setTextColor(getResources().getColor(android.R.color.darker_gray));
        
        btnFavouriteCategories.setBackgroundResource(R.drawable.button_white);
        btnFavouriteCategories.setTextColor(getResources().getColor(android.R.color.darker_gray));
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(categoryName);
        }
    }

    private void setupProductList() {
        productList = new ArrayList<>();
        
        // Filter products by category
        // For now, showing sample products. TODO: Filter by actual category from Firebase
        if ("1".equals(categoryId)) { // Beverages
            productList.add(new Product("1", "Mojo", "Soft Drink", 25.0, "250ml", "", "Akij Food & Beverage Ltd. (AFBL)", 0, false));
            productList.add(new Product("5", "Shezan Mango Juice", "Mango Juice", 120.0, "1L", "", "Sajeeb Group", 0, false));
            productList.add(new Product("3", "Spa Drinking Water", "Water", 20.0, "500ml", "", "Akij Food & Beverage Ltd. (AFBL)", 0, false));
        } else if ("2".equals(categoryId)) { // Hair Care
            productList.add(new Product("10", "Jui HairCare Oil", "Hair Oil", 95.0, "200ml", "", "Square Toiletries Ltd.", 0, false));
        } else if ("3".equals(categoryId)) { // Oral Care
            productList.add(new Product("2", "MediPlus DS", "Toothpaste", 85.0, "100g", "", "Anfords Bangladesh Ltd.", 0, false));
        } else if ("4".equals(categoryId)) { // Snacks
            productList.add(new Product("7", "Ruchi BBQ Chanachur", "Snack", 30.0, "150g", "", "Pran Foods Ltd.", 0, false));
            productList.add(new Product("6", "Pran Potata Spicy", "Biscuit", 40.0, "200g", "", "Pran Foods Ltd.", 0, false));
        } else if ("5".equals(categoryId)) { // Food & Grocery
            productList.add(new Product("11", "Radhuni Turmeric", "Powder", 55.0, "100g", "", "Square Food & Beverage Ltd.", 0, false));
            productList.add(new Product("12", "Pran Premium Ghee", "Cooking Ghee", 250.0, "500g", "", "Pran Dairy Ltd.", 0, false));
        } else if ("6".equals(categoryId)) { // Home Care
            productList.add(new Product("8", "Bashundhara Towel", "Hand Towel", 80.0, "pack", "", "Bashundhara Paper Mills PLC", 0, false));
        } else if ("7".equals(categoryId)) { // Skin Care
            productList.add(new Product("9", "Revive Perfect Skin", "Moisturizing Lotion", 150.0, "100ml", "", "Square Toiletries Ltd.", 0, false));
            productList.add(new Product("4", "Meril Milk Soap", "Moisturizing Soap", 35.0, "75g", "", "Square Toiletries Ltd.", 0, false));
        }
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(this, productList);
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvProducts.setAdapter(productAdapter);
        rvProducts.setHasFixedSize(false);
        
        // Calculate and set minimum height
        int itemHeight = (int) (400 * getResources().getDisplayMetrics().density);
        int rows = (int) Math.ceil(productList.size() / 2.0);
        int extraPadding = (int) (100 * getResources().getDisplayMetrics().density);
        int totalHeight = (rows * itemHeight) + extraPadding;
        rvProducts.setMinimumHeight(totalHeight);
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
