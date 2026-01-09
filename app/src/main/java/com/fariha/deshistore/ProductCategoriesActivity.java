package com.fariha.deshistore;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProductCategoriesActivity extends AppCompatActivity {

    private RecyclerView rvCategories;
    private List<String> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_categories);

        initializeViews();
        setupCategoryList();
    }

    private void initializeViews() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Product Categories");
        }
        
        rvCategories = findViewById(R.id.rvProducts);
    }

    private void setupCategoryList() {
        categoryList = new ArrayList<>();
        // TODO: Load categories from Firebase/Database
        Toast.makeText(this, "Loading categories...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
