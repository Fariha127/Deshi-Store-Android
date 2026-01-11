package com.fariha.deshistore;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class NewlyAddedActivity extends AppCompatActivity {

    private static final String TAG = "NewlyAddedActivity";
    
    private RecyclerView rvNewProducts;
    private EditText etSearch;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private Button btnHome, btnAllProducts, btnProductCategories, btnNewlyAdded, btnMyFavourites, btnFavouriteCategories;
    private Button btnLogin, btnSignUp;
    
    private FirebaseFirestore db;
    private ListenerRegistration productsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newly_added);

        db = FirebaseFirestore.getInstance();
        
        initializeViews();
        setupRecyclerView();
        setupClickListeners();
        setupSearch();
        loadNewlyAddedProducts();
    }

    private void initializeViews() {
        rvNewProducts = findViewById(R.id.rvProducts);
        etSearch = findViewById(R.id.etSearch);
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
            getSupportActionBar().setTitle("Newly Added Products");
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
            // Already on Newly Added
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

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (productAdapter != null) {
                    productAdapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void loadNewlyAddedProducts() {
        Log.d(TAG, "Loading newly added products from Firestore...");
        
        // Load the 12 most recently added approved products
        // Using document ID as a proxy for creation order (Firestore auto-generated IDs are time-based)
        productsListener = db.collection("products")
                .whereEqualTo("status", "approved")
                .limit(12)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Error loading products: " + error.getMessage());
                        Toast.makeText(this, "Error loading products", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshots != null) {
                        productList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            Product product = doc.toObject(Product.class);
                            product.setId(doc.getId());
                            productList.add(product);
                            Log.d(TAG, "Loaded product: " + product.getName());
                        }
                        
                        Log.d(TAG, "Total newly added products loaded: " + productList.size());
                        productAdapter.updateProductList(productList);
                    }
                });
    }

    private void setupRecyclerView() {
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productList);
        rvNewProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvNewProducts.setAdapter(productAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (productsListener != null) {
            productsListener.remove();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
