package com.fariha.deshistore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VendorDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnAddProduct, btnProductStatus, btnLogout;
    private ProductAdapter adapter;
    private List<Product> productList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String vendorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_dashboard);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        vendorId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "";

        recyclerView = findViewById(R.id.recyclerView);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        btnProductStatus = findViewById(R.id.btnProductStatus);
        btnLogout = findViewById(R.id.btnLogout);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        productList = new ArrayList<>();
        adapter = new ProductAdapter(this, productList);
        recyclerView.setAdapter(adapter);

        btnAddProduct.setOnClickListener(v -> {
            startActivity(new Intent(this, AddProductActivity.class));
        });

        btnProductStatus.setOnClickListener(v -> {
            startActivity(new Intent(this, ProductStatusActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        loadApprovedProducts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadApprovedProducts();
    }

    private void loadApprovedProducts() {
        mDatabase.child("products")
                .orderByChild("vendorId")
                .equalTo(vendorId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        productList.clear();
                        for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                            Product product = productSnapshot.getValue(Product.class);
                            if (product != null && "approved".equals(product.getStatus())) {
                                product.setProductId(productSnapshot.getKey());
                                productList.add(product);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(VendorDashboardActivity.this, "Error loading products", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
