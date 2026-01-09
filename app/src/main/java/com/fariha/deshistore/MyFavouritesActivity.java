package com.fariha.deshistore;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyFavouritesActivity extends AppCompatActivity {

    private RecyclerView rvFavourites;
    private ProductAdapter productAdapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favourites);

        initializeViews();
        setupProductList();
        setupRecyclerView();
    }

    private void initializeViews() {
        rvFavourites = findViewById(R.id.rvProducts);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Favourites");
        }
    }

    private void setupProductList() {
        productList = new ArrayList<>();
        // TODO: Load favourite products from Firebase/Database
        Toast.makeText(this, "Loading favourite products...", Toast.LENGTH_SHORT).show();
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(this, productList);
        rvFavourites.setLayoutManager(new GridLayoutManager(this, 2));
        rvFavourites.setAdapter(productAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
