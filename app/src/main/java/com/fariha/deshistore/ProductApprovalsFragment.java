package com.fariha.deshistore;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProductApprovalsFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button btnRefresh;
    private ProductApprovalAdapter adapter;
    private List<Product> productList;
    private FirebaseFirestore mDatabase;
    private ListenerRegistration productsListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        android.util.Log.d("ProductApprovals", "onCreateView called");
        View view = inflater.inflate(R.layout.fragment_product_approvals, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        btnRefresh = view.findViewById(R.id.btnRefresh);
        android.util.Log.d("ProductApprovals", "Views found: recyclerView=" + (recyclerView != null));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        productList = new ArrayList<>();
        adapter = new ProductApprovalAdapter(productList, this::approveProduct, this::rejectProduct);
        recyclerView.setAdapter(adapter);

        mDatabase = FirebaseFirestore.getInstance();

        btnRefresh.setOnClickListener(v -> loadPendingProducts());

        loadPendingProducts();

        return view;
    }

    private void loadPendingProducts() {
        android.util.Log.d("ProductApprovals", "loadPendingProducts called");
        
        // Check authentication state
        com.google.firebase.auth.FirebaseUser currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        android.util.Log.d("ProductApprovals", "Current user: " + (currentUser != null ? currentUser.getEmail() : "NULL - NOT AUTHENTICATED"));
        
        if (currentUser == null) {
            Toast.makeText(getContext(), "Error: Not authenticated!", Toast.LENGTH_LONG).show();
            return;
        }
        
        if (productsListener != null) {
            productsListener.remove();
        }
        
        productsListener = mDatabase.collection("products")
                .whereEqualTo("status", "pending")
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        android.util.Log.e("ProductApprovals", "Error loading products: " + error.getMessage(), error);
                        Toast.makeText(getContext(), "Error loading products: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    
                    productList.clear();
                    if (queryDocumentSnapshots != null) {
                        android.util.Log.d("ProductApprovals", "Found " + queryDocumentSnapshots.size() + " pending products");
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Product product = document.toObject(Product.class);
                            product.setProductId(document.getId());
                            android.util.Log.d("ProductApprovals", "Product: " + product.getName() + " ID: " + document.getId());
                            productList.add(product);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Loaded " + productList.size() + " pending products", Toast.LENGTH_SHORT).show();
                });
    }

    private void approveProduct(Product product) {
        String productId = product.getProductId();
        if (productId == null || productId.isEmpty()) {
            Toast.makeText(getContext(), "Error: Product ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        
        new AlertDialog.Builder(getContext())
                .setTitle("Approve Product")
                .setMessage("Are you sure you want to approve this product?")
                .setPositiveButton("Approve", (dialog, which) -> {
                    mDatabase.collection("products").document(productId)
                            .update("status", "approved")
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Product approved", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                android.util.Log.e("ProductApprovals", "Failed to approve: " + e.getMessage(), e);
                                Toast.makeText(getContext(), "Failed to approve: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void rejectProduct(Product product) {
        String productId = product.getProductId();
        if (productId == null || productId.isEmpty()) {
            Toast.makeText(getContext(), "Error: Product ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Reject Product");
        builder.setMessage("Enter rejection reason:");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setHint("Reason for rejection");
        builder.setView(input);

        builder.setPositiveButton("Reject", (dialog, which) -> {
            String reason = input.getText().toString().trim();
            if (reason.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a rejection reason", Toast.LENGTH_SHORT).show();
                return;
            }

            java.util.Map<String, Object> updates = new java.util.HashMap<>();
            updates.put("status", "rejected");
            updates.put("rejectionReason", reason);
            
            mDatabase.collection("products").document(productId)
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Product rejected", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        android.util.Log.e("ProductApprovals", "Failed to reject: " + e.getMessage(), e);
                        Toast.makeText(getContext(), "Failed to reject: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (productsListener != null) {
            productsListener.remove();
        }
    }
}
