package com.fariha.deshistore;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Intent;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProductStatusAdapter extends RecyclerView.Adapter<ProductStatusAdapter.ViewHolder> {

    private List<Product> productList;

    public ProductStatusAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_status, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        
        holder.tvProductId.setText(product.getProductId());
        holder.tvName.setText(product.getName());
        holder.tvCategory.setText(product.getCategory());
        holder.tvPrice.setText("৳ " + product.getPrice());
        
        String status = product.getStatus() != null ? product.getStatus().toUpperCase() : "PENDING";
        holder.tvStatus.setText(status);
        
        // Set status color
        switch (status) {
            case "APPROVED":
                holder.tvStatus.setTextColor(0xFF4CAF50); // Green
                break;
            case "REJECTED":
                holder.tvStatus.setTextColor(0xFFF44336); // Red
                if (product.getRejectionReason() != null) {
                    holder.tvRejectionReason.setVisibility(View.VISIBLE);
                    holder.tvRejectionReason.setText("Reason: " + product.getRejectionReason());
                } else {
                    holder.tvRejectionReason.setVisibility(View.GONE);
                }
                break;
            case "PENDING":
            default:
                holder.tvStatus.setTextColor(0xFFFF9800); // Orange
                holder.tvRejectionReason.setVisibility(View.GONE);
                break;
        }
        
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            String imageUrl = product.getImageUrl();
            if (imageUrl.startsWith("data:image")) {
                try {
                    String base64Data = imageUrl.substring(imageUrl.indexOf(",") + 1);
                    byte[] decodedBytes = Base64.decode(base64Data, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    holder.ivProduct.setImageBitmap(bitmap);
                } catch (Exception e) {
                    holder.ivProduct.setImageResource(R.drawable.ic_launcher_foreground);
                }
            } else {
                Glide.with(holder.itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(holder.ivProduct);
            }
        }
        
        // Add click listener to view/edit product details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), VendorProductDetailsActivity.class);
            intent.putExtra("product_id", product.getProductId());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvProductId, tvName, tvCategory, tvPrice, tvStatus, tvRejectionReason;

        ViewHolder(View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            tvProductId = itemView.findViewById(R.id.tvProductId);
            tvName = itemView.findViewById(R.id.tvName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvRejectionReason = itemView.findViewById(R.id.tvRejectionReason);
        }
    }
}
