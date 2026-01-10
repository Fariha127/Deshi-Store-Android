package com.fariha.deshistore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProductApprovalAdapter extends RecyclerView.Adapter<ProductApprovalAdapter.ViewHolder> {

    private List<Product> productList;
    private OnApproveListener approveListener;
    private OnRejectListener rejectListener;

    public interface OnApproveListener {
        void onApprove(Product product);
    }

    public interface OnRejectListener {
        void onReject(Product product);
    }

    public ProductApprovalAdapter(List<Product> productList, OnApproveListener approveListener, OnRejectListener rejectListener) {
        this.productList = productList;
        this.approveListener = approveListener;
        this.rejectListener = rejectListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_approval, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvProductId.setText(product.getProductId());
        holder.tvName.setText(product.getName());
        holder.tvCategory.setText(product.getCategory());
        holder.tvPrice.setText("৳ " + product.getPrice());
        holder.tvVendorId.setText("Vendor: " + product.getVendorId());
        
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.ivProduct);
        }

        holder.btnApprove.setOnClickListener(v -> approveListener.onApprove(product));
        holder.btnReject.setOnClickListener(v -> rejectListener.onReject(product));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvProductId, tvName, tvCategory, tvPrice, tvVendorId;
        Button btnApprove, btnReject;

        ViewHolder(View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            tvProductId = itemView.findViewById(R.id.tvProductId);
            tvName = itemView.findViewById(R.id.tvName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvVendorId = itemView.findViewById(R.id.tvVendorId);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
