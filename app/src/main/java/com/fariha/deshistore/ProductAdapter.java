package com.fariha.deshistore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.tvProductName.setText(product.getName());
        holder.tvProductCategory.setText(product.getCategory());
        holder.tvProductPrice.setText(String.format(Locale.getDefault(), "৳ %.0f", product.getPrice()));
        holder.tvProductUnit.setText("/" + product.getUnit());
        holder.tvRecommendCount.setText(product.getRecommendCount() + " Recommends");

        // Update favorite icon
        if (product.isFavorite()) {
            holder.btnFavorite.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            holder.btnFavorite.setImageResource(android.R.drawable.btn_star_big_off);
        }

        // Load image if URL is provided
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            // Use image loading library like Glide or Picasso
            // Glide.with(context).load(product.getImageUrl()).into(holder.ivProductImage);
        }

        // Favorite button click
        holder.btnFavorite.setOnClickListener(v -> {
            product.setFavorite(!product.isFavorite());
            notifyItemChanged(position);
            
            if (product.isFavorite()) {
                Toast.makeText(context, product.getName() + " added to favorites", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, product.getName() + " removed from favorites", Toast.LENGTH_SHORT).show();
            }
            // TODO: Update favorite status in database
        });

        // View Details button click
        holder.btnViewDetails.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(context, ProductDetailsActivity.class);
            intent.putExtra("product_id", product.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvProductCategory, tvProductPrice, tvProductUnit, tvRecommendCount;
        ImageButton btnFavorite;
        Button btnViewDetails;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductCategory = itemView.findViewById(R.id.tvProductCategory);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductUnit = itemView.findViewById(R.id.tvProductUnit);
            tvRecommendCount = itemView.findViewById(R.id.tvRecommendCount);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }

    public void updateProductList(List<Product> newProductList) {
        this.productList = newProductList;
        notifyDataSetChanged();
    }
}
