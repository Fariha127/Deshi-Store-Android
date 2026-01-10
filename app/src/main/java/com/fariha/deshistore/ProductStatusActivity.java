package com.fariha.deshistore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductStatusActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnBackToDashboard;
    private ProductStatusAdapter adapter;
    private List<Product> productList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String vendorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_status);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
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
        mDatabase.child("products")
                .orderByChild("vendorId")
                .equalTo(vendorId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        productList.clear();
                        for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                            Product product = productSnapshot.getValue(Product.class);
                            if (product != null) {
                                product.setProductId(productSnapshot.getKey());
                                productList.add(product);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        Toast.makeText(ProductStatusActivity.this, 
                                "Loaded " + productList.size() + " products", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(ProductStatusActivity.this, "Error loading products", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
