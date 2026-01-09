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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
        
        // Check if user is logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        boolean isLoggedIn = (currentUser != null);
        
        // Update favorite button appearance - only show filled heart if logged in and favorited
        if (isLoggedIn && isFavorite) {
            holder.btnFavorite.setText("♥");
            holder.btnFavorite.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.btnFavorite.setText("♡");
            holder.btnFavorite.setTextColor(context.getResources().getColor(android.R.color.holo_red_light));
        }

        // Load image if URL is provided
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.ivProductImage);
        } else {
            holder.ivProductImage.setImageResource(R.drawable.ic_launcher_background);
        }

        // Favorite button click
        holder.btnFavorite.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Toast.makeText(context, "Please log in first to add favorites", Toast.LENGTH_SHORT).show();
                return;
            }
            
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

        // Rate button click - navigate to product details
        holder.btnRate.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailsActivity.class);
            intent.putExtra("product_id", product.getId());
            context.startActivity(intent);
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
