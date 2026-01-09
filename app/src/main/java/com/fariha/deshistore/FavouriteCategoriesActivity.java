package com.fariha.deshistore;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FavouriteCategoriesActivity extends AppCompatActivity {

    private RecyclerView rvFavouriteCategories;
    private List<String> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_categories);

        initializeViews();
        setupCategoryList();
    }

    private void initializeViews() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Favourite Categories");
        }
        
        rvFavouriteCategories = findViewById(R.id.rvProducts);
    }

    private void setupCategoryList() {
        categoryList = new ArrayList<>();
        // TODO: Load favourite categories from Firebase/Database
        Toast.makeText(this, "Loading favourite categories...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
