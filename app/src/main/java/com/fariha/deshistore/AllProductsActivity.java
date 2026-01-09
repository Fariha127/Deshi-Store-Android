package com.fariha.deshistore;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AllProductsActivity extends AppCompatActivity {

    private RecyclerView rvAllProducts;
    private ProductAdapter productAdapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_products);

        initializeViews();
        setupProductList();
        setupRecyclerView();
    }

    private void initializeViews() {
        rvAllProducts = findViewById(R.id.rvProducts);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("All Products");
        }
    }

    private void setupProductList() {
        productList = new ArrayList<>();
        // TODO: Load all products from Firebase/Database
        Toast.makeText(this, "Loading all products...", Toast.LENGTH_SHORT).show();
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(this, productList);
        rvAllProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvAllProducts.setAdapter(productAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
