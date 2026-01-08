package com.arjundabbe.jivanman.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arjundabbe.jivanman.R;
import com.arjundabbe.jivanman.models.SavedArticle;
import com.arjundabbe.jivanman.ui.NewsDetailActivity;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView Adapter for displaying saved/bookmarked articles
 * Supports:
 * - Opening full news on item click
 * - Delete via button click
 * - Share via button click
 */
public class SavedArticleAdapter extends RecyclerView.Adapter<SavedArticleAdapter.ViewHolder> {

    // Interface for item actions (delete)
    public interface OnItemActionListener {
        void onDelete(SavedArticle article, int position);
    }

    private final Context context;
    private final List<SavedArticle> savedList;
    private final OnItemActionListener listener;

    public SavedArticleAdapter(Context context, List<SavedArticle> savedList, OnItemActionListener listener) {
        this.context = context;
        this.savedList = savedList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_saved_article, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        SavedArticle article = savedList.get(position);

        // Set title
        holder.title.setText(article.getTitle());

        // Load image with Glide
        Glide.with(context)
                .load(article.getImageUrl())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.image);

        // 👉 Open NewsDetailActivity on card click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NewsDetailActivity.class);

            intent.putExtra("newsId", article.getNewsId());
            intent.putExtra("title", article.getTitle());
            intent.putExtra("description", article.getDescription());
            intent.putExtra("imageUrl", article.getImageUrl());
            intent.putExtra("date", article.getDate());
            intent.putExtra("reporter", article.getReporter());
            intent.putStringArrayListExtra("category",
                    new ArrayList<>(article.getCategory()));

            context.startActivity(intent);
        });

        // ❌ Remove/Delete button
        holder.removeButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(article, holder.getAdapterPosition());
            }
        });

        // Share button
        holder.shareButton.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    article.getTitle() + "\n\n" + article.getDescription());
            context.startActivity(Intent.createChooser(shareIntent, "Share via"));
        });
    }

    @Override
    public int getItemCount() {
        return savedList.size();
    }

    /**
     * ViewHolder for saved article item
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title;
        ImageButton removeButton, shareButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.articleImage);
            title = itemView.findViewById(R.id.articleTitle);
            removeButton = itemView.findViewById(R.id.removeButton);
            shareButton = itemView.findViewById(R.id.shareButton);
        }
    }
}
