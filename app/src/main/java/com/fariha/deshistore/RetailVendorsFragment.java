package com.fariha.deshistore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class RetailVendorsFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button btnRefresh;
    private VendorListAdapter adapter;
    private List<Vendor> vendorList;
    private FirebaseFirestore mDatabase;
    private ListenerRegistration vendorsListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vendors, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        btnRefresh = view.findViewById(R.id.btnRefresh);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        vendorList = new ArrayList<>();
        adapter = new VendorListAdapter(vendorList);
        recyclerView.setAdapter(adapter);

        mDatabase = FirebaseFirestore.getInstance();

        btnRefresh.setOnClickListener(v -> loadVendors());

        loadVendors();

        return view;
    }

    private void loadVendors() {
        Toast.makeText(getContext(), "Loading retail vendors...", Toast.LENGTH_SHORT).show();
        
        // Remove old listener if exists
        if (vendorsListener != null) {
            vendorsListener.remove();
        }
        
        // Use real-time listener for automatic updates from Firestore
        vendorsListener = mDatabase.collection("users")
                .whereEqualTo("userType", "Retail Vendor")
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        android.util.Log.e("RetailVendors", "Error: " + error.getMessage());
                        Toast.makeText(getContext(), "Error loading vendors: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    vendorList.clear();
                    if (queryDocumentSnapshots != null) {
                        android.util.Log.d("RetailVendors", "Total retail vendors: " + queryDocumentSnapshots.size());
                        
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Vendor vendor = new Vendor();
                            vendor.setVendorId(document.getId());
                            vendor.setShopName(document.getString("shopName"));
                            vendor.setOwnerName(document.getString("ownerName"));
                            vendor.setEmail(document.getString("email"));
                            vendor.setPhoneNumber(document.getString("phoneNumber"));
                            vendor.setStatus(document.getString("status"));
                            
                            android.util.Log.d("RetailVendors", "Added: " + vendor.getShopName() + ", Email: " + vendor.getEmail());
                            vendorList.add(vendor);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Loaded " + vendorList.size() + " retail vendors", Toast.LENGTH_LONG).show();
                    android.util.Log.d("RetailVendors", "Final count: " + vendorList.size());
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Remove listener to prevent memory leaks
        if (vendorsListener != null) {
            vendorsListener.remove();
        }
    }
}
