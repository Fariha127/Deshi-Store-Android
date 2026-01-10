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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductApprovalsFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button btnRefresh;
    private ProductApprovalAdapter adapter;
    private List<Product> productList;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_approvals, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        btnRefresh = view.findViewById(R.id.btnRefresh);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        productList = new ArrayList<>();
        adapter = new ProductApprovalAdapter(productList, this::approveProduct, this::rejectProduct);
        recyclerView.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        btnRefresh.setOnClickListener(v -> loadPendingProducts());

        loadPendingProducts();

        return view;
    }

    private void loadPendingProducts() {
        mDatabase.child("products").orderByChild("status").equalTo("pending")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productList.clear();
                        for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                            Product product = productSnapshot.getValue(Product.class);
                            if (product != null) {
                                product.setProductId(productSnapshot.getKey());
                                productList.add(product);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Loaded " + productList.size() + " pending products", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error loading products", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void approveProduct(Product product) {
        new AlertDialog.Builder(getContext())
                .setTitle("Approve Product")
                .setMessage("Are you sure you want to approve this product?")
                .setPositiveButton("Approve", (dialog, which) -> {
                    mDatabase.child("products").child(product.getProductId()).child("status").setValue("approved")
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Product approved", Toast.LENGTH_SHORT).show();
                                loadPendingProducts();
                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to approve", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void rejectProduct(Product product) {
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

            mDatabase.child("products").child(product.getProductId()).child("status").setValue("rejected");
            mDatabase.child("products").child(product.getProductId()).child("rejectionReason").setValue(reason)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Product rejected", Toast.LENGTH_SHORT).show();
                        loadPendingProducts();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to reject", Toast.LENGTH_SHORT).show());
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
