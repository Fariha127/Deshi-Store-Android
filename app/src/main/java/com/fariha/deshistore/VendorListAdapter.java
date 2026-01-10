package com.fariha.deshistore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VendorListAdapter extends RecyclerView.Adapter<VendorListAdapter.ViewHolder> {

    private List<Vendor> vendorList;

    public VendorListAdapter(List<Vendor> vendorList) {
        this.vendorList = vendorList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vendor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Vendor vendor = vendorList.get(position);
        holder.tvVendorId.setText(vendor.getVendorId());
        
        String name = vendor.getCompanyName() != null ? vendor.getCompanyName() : vendor.getShopName();
        holder.tvName.setText(name);
        
        String contact = vendor.getContactPerson() != null ? vendor.getContactPerson() : vendor.getOwnerName();
        holder.tvContact.setText(contact);
        
        holder.tvEmail.setText(vendor.getEmail());
        holder.tvPhone.setText(vendor.getPhoneNumber());
        holder.tvStatus.setText(vendor.getStatus() != null ? vendor.getStatus() : "approved");
    }

    @Override
    public int getItemCount() {
        return vendorList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvVendorId, tvName, tvContact, tvEmail, tvPhone, tvStatus;

        ViewHolder(View itemView) {
            super(itemView);
            tvVendorId = itemView.findViewById(R.id.tvVendorId);
            tvName = itemView.findViewById(R.id.tvName);
            tvContact = itemView.findViewById(R.id.tvContact);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
