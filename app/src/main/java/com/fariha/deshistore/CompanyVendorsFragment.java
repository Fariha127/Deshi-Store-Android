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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CompanyVendorsFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button btnRefresh;
    private VendorListAdapter adapter;
    private List<Vendor> vendorList;
    private DatabaseReference mDatabase;

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

        mDatabase = FirebaseDatabase.getInstance().getReference();

        btnRefresh.setOnClickListener(v -> loadVendors());

        loadVendors();

        return view;
    }

    private void loadVendors() {
        mDatabase.child("users").orderByChild("userType").equalTo("Company Vendor")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        vendorList.clear();
                        for (DataSnapshot vendorSnapshot : snapshot.getChildren()) {
                            Vendor vendor = new Vendor();
                            vendor.setVendorId(vendorSnapshot.getKey());
                            vendor.setCompanyName(vendorSnapshot.child("companyName").getValue(String.class));
                            vendor.setContactPerson(vendorSnapshot.child("fullName").getValue(String.class));
                            vendor.setEmail(vendorSnapshot.child("email").getValue(String.class));
                            vendor.setPhoneNumber(vendorSnapshot.child("phoneNumber").getValue(String.class));
                            vendor.setStatus(vendorSnapshot.child("status").getValue(String.class));
                            vendorList.add(vendor);
                        }
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Loaded " + vendorList.size() + " vendors", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error loading vendors", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
