package com.fariha.deshistore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProductStatusActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnBackToDashboard;
    private ProductStatusAdapter adapter;
    private List<Product> productList;
    private FirebaseFirestore mDatabase;
    private FirebaseAuth mAuth;
    private String vendorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_status);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();
        vendorId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "";

        recyclerView = findViewById(R.id.recyclerView);
        btnBackToDashboard = findViewById(R.id.btnBackToDashboard);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        adapter = new ProductStatusAdapter(productList);
        recyclerView.setAdapter(adapter);

        btnBackToDashboard.setOnClickListener(v -> finish());

        loadAllProducts();
    }

    private void loadAllProducts() {
        mDatabase.collection("products")
                .whereEqualTo("vendorId", vendorId)
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        Toast.makeText(ProductStatusActivity.this, "Error loading products", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    productList.clear();
                    if (queryDocumentSnapshots != null) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Product product = document.toObject(Product.class);
                            product.setProductId(document.getId());
                            productList.add(product);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    Toast.makeText(ProductStatusActivity.this, 
                            "Loaded " + productList.size() + " products", Toast.LENGTH_SHORT).show();
                });
    }
}
