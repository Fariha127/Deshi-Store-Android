package com.fariha.deshistore;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private static final String PREFS_NAME = "FavoritesPrefs";
    private static final String FAVORITES_KEY = "favorites";

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

        // Check if product is in favorites
        boolean isFavorite = isFavoriteProduct(product.getId());
        
        // Update favorite button appearance
        if (isFavorite) {
            holder.btnFavorite.setText("♥");
            holder.btnFavorite.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.btnFavorite.setText("♡");
            holder.btnFavorite.setTextColor(context.getResources().getColor(android.R.color.holo_red_light));
        }

        // Load image if URL is provided
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            // Use image loading library like Glide or Picasso
            // Glide.with(context).load(product.getImageUrl()).into(holder.ivProductImage);
        }

        // Favorite button click
        holder.btnFavorite.setOnClickListener(v -> {
            if (isFavoriteProduct(product.getId())) {
                removeFavorite(product.getId());
                holder.btnFavorite.setText("♡");
                holder.btnFavorite.setTextColor(context.getResources().getColor(android.R.color.holo_red_light));
                Toast.makeText(context, product.getName() + " removed from favorites", Toast.LENGTH_SHORT).show();
            } else {
                addFavorite(product.getId());
                holder.btnFavorite.setText("♥");
                holder.btnFavorite.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                Toast.makeText(context, product.getName() + " added to favorites", Toast.LENGTH_SHORT).show();
            }
        });

        // Rate button click
        holder.btnRate.setOnClickListener(v -> {
            Toast.makeText(context, "Rate " + product.getName(), Toast.LENGTH_SHORT).show();
            // TODO: Open rating dialog
        });

        // Card click - view details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailsActivity.class);
            intent.putExtra("product_id", product.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // Favorites management methods
    private boolean isFavoriteProduct(String productId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> favorites = prefs.getStringSet(FAVORITES_KEY, new HashSet<>());
        return favorites.contains(productId);
    }

    private void addFavorite(String productId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> favorites = new HashSet<>(prefs.getStringSet(FAVORITES_KEY, new HashSet<>()));
        favorites.add(productId);
        prefs.edit().putStringSet(FAVORITES_KEY, favorites).apply();
    }

    private void removeFavorite(String productId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> favorites = new HashSet<>(prefs.getStringSet(FAVORITES_KEY, new HashSet<>()));
        favorites.remove(productId);
        prefs.edit().putStringSet(FAVORITES_KEY, favorites).apply();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvProductCategory, tvProductPrice, tvProductUnit;
        Button btnFavorite, btnRate;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductCategory = itemView.findViewById(R.id.tvProductCategory);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductUnit = itemView.findViewById(R.id.tvProductUnit);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            btnRate = itemView.findViewById(R.id.btnRate);
        }
    }

    public void updateProductList(List<Product> newProductList) {
        this.productList = newProductList;
        notifyDataSetChanged();
    }
}
