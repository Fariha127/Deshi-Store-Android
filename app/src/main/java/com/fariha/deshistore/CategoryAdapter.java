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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<Category> categoryList;
    private static final String PREFS_NAME = "FavoritesPrefs";
    private static final String FAVORITE_CATEGORIES_KEY = "favorite_categories";

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);

        holder.tvCategoryName.setText(category.getName());
        holder.tvCategoryDescription.setText(category.getDescription());

        // Load category image
        if (category.getImageUrl() != null && !category.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(category.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.ivCategoryImage);
        } else {
            holder.ivCategoryImage.setImageResource(R.drawable.ic_launcher_background);
        }

        // Check if category is in favorites
        boolean isFavorite = isFavoriteCategory(category.getId());
        
        // Update favorite button appearance
        if (isFavorite) {
            holder.btnFavorite.setText("♥");
            holder.btnFavorite.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.btnFavorite.setText("♡");
            holder.btnFavorite.setTextColor(context.getResources().getColor(android.R.color.holo_red_light));
        }

        // Favorite button click
        holder.btnFavorite.setOnClickListener(v -> {
            if (isFavoriteCategory(category.getId())) {
                removeFavoriteCategory(category.getId());
                holder.btnFavorite.setText("♡");
                holder.btnFavorite.setTextColor(context.getResources().getColor(android.R.color.holo_red_light));
                Toast.makeText(context, category.getName() + " removed from favorites", Toast.LENGTH_SHORT).show();
            } else {
                addFavoriteCategory(category.getId());
                holder.btnFavorite.setText("♥");
                holder.btnFavorite.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                Toast.makeText(context, category.getName() + " added to favorites", Toast.LENGTH_SHORT).show();
            }
        });

        // View button click
        holder.btnView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CategoryProductsActivity.class);
            intent.putExtra("category_id", category.getId());
            intent.putExtra("category_name", category.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    // Favorites management methods
    private boolean isFavoriteCategory(String categoryId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> favorites = prefs.getStringSet(FAVORITE_CATEGORIES_KEY, new HashSet<>());
        return favorites.contains(categoryId);
    }

    private void addFavoriteCategory(String categoryId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> favorites = new HashSet<>(prefs.getStringSet(FAVORITE_CATEGORIES_KEY, new HashSet<>()));
        favorites.add(categoryId);
        prefs.edit().putStringSet(FAVORITE_CATEGORIES_KEY, favorites).apply();
    }

    private void removeFavoriteCategory(String categoryId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> favorites = new HashSet<>(prefs.getStringSet(FAVORITE_CATEGORIES_KEY, new HashSet<>()));
        favorites.remove(categoryId);
        prefs.edit().putStringSet(FAVORITE_CATEGORIES_KEY, favorites).apply();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCategoryImage;
        TextView tvCategoryName, tvCategoryDescription;
        Button btnFavorite, btnView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCategoryImage = itemView.findViewById(R.id.ivCategoryImage);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvCategoryDescription = itemView.findViewById(R.id.tvCategoryDescription);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            btnView = itemView.findViewById(R.id.btnView);
        }
    }

    public void updateCategoryList(List<Category> newCategoryList) {
        this.categoryList = newCategoryList;
        notifyDataSetChanged();
    }
}
