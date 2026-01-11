package com.fariha.deshistore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private Context context;
    private List<Review> reviewList;
    private SimpleDateFormat dateFormat;

    public ReviewAdapter(Context context, List<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);

        holder.tvReviewerName.setText(review.getReviewerName());
        holder.tvReviewText.setText(review.getReviewText());
        
        // Handle date (might be null)
        if (review.getDate() != null) {
            holder.tvReviewDate.setText(dateFormat.format(review.getDate()));
        } else {
            holder.tvReviewDate.setText("Recently");
        }

        // Display stars based on rating
        String stars = getStarsString(review.getRating());
        holder.tvReviewStars.setText(stars);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    private String getStarsString(int rating) {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            if (i < rating) {
                stars.append("★");
            } else {
                stars.append("☆");
            }
        }
        return stars.toString();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvReviewerName, tvReviewStars, tvReviewDate, tvReviewText;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReviewerName = itemView.findViewById(R.id.tvReviewerName);
            tvReviewStars = itemView.findViewById(R.id.tvReviewStars);
            tvReviewDate = itemView.findViewById(R.id.tvReviewDate);
            tvReviewText = itemView.findViewById(R.id.tvReviewText);
        }
    }

    public void updateReviewList(List<Review> newReviewList) {
        this.reviewList = newReviewList;
        notifyDataSetChanged();
    }
}
