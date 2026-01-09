package com.fariha.deshistore;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NewlyAddedActivity extends AppCompatActivity {

    private RecyclerView rvNewProducts;
    private ProductAdapter productAdapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newly_added);

        initializeViews();
        setupProductList();
        setupRecyclerView();
    }

    private void initializeViews() {
        rvNewProducts = findViewById(R.id.rvProducts);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Newly Added Products");
        }
    }

    private void setupProductList() {
        productList = new ArrayList<>();
        // TODO: Load newly added products from Firebase/Database
        Toast.makeText(this, "Loading newly added products...", Toast.LENGTH_SHORT).show();
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(this, productList);
        rvNewProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvNewProducts.setAdapter(productAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
