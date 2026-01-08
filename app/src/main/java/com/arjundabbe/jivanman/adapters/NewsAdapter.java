package com.arjundabbe.jivanman.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arjundabbe.jivanman.R;
import com.arjundabbe.jivanman.models.NewsArticle;
import com.arjundabbe.jivanman.ui.NewsDetailActivity;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * NewsAdapter
 * ------------
 * RecyclerView Adapter used to display news articles
 * on Home screen and category screens.
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private static final String TAG = "NewsAdapter";

    // Context for inflating layouts and starting activities
    private final Context context;

    // List of news articles
    private List<NewsArticle> newsList;

    /**
     * Constructor
     */
    public NewsAdapter(Context context, List<NewsArticle> newsList) {
        this.context = context;
        this.newsList = newsList;
        Log.d(TAG, "NewsAdapter initialized with " + newsList.size() + " articles");
    }

    /**
     * Inflate item_news layout and create ViewHolder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_news, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Bind data to each RecyclerView item
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        NewsArticle article = newsList.get(position);

        // Set title and description
        holder.title.setText(article.getTitle());
        holder.description.setText(article.getDescription());

        // Load image using Glide
        Glide.with(context)
                .load(article.getImageUrl())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.image);

        // ---------------------------------
        // Open NewsDetailActivity on click
        // ---------------------------------
        holder.itemView.setOnClickListener(v -> {

            Log.d(TAG, "Opening details for -> " + article.getTitle());

            Intent intent = new Intent(context, NewsDetailActivity.class);

            // Pass all required news data
            intent.putExtra("newsId", article.getNewsId());     // REQUIRED
            intent.putExtra("title", article.getTitle());
            intent.putExtra("description", article.getDescription());
            intent.putExtra("imageUrl", article.getImageUrl());
            intent.putExtra("date", article.getDateTime());
            intent.putExtra("reporter", article.getReporter());

            // Pass category list safely
            if (article.getCategory() != null) {
                intent.putStringArrayListExtra(
                        "category",
                        new ArrayList<>(article.getCategory())
                );
            }

            context.startActivity(intent);
        });

        // ---------------------------------
        // Save button (future feature)
        // ---------------------------------
        if (holder.btnSave != null) {
            holder.btnSave.setOnClickListener(v ->
                    Toast.makeText(
                            context,
                            "Feature येणार आहे: लेख सेव्ह करा",
                            Toast.LENGTH_SHORT
                    ).show()
            );
        }

        // ---------------------------------
        // Share button
        // ---------------------------------
        if (holder.btnShare != null) {
            holder.btnShare.setOnClickListener(v -> {

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");

                shareIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        article.getTitle() + "\n\n" + article.getDescription()
                );

                context.startActivity(
                        Intent.createChooser(
                                shareIntent,
                                "कशाद्वारे शेअर करायचे ते निवडा"
                        )
                );
            });
        }
    }

    /**
     * Return total item count
     */
    @Override
    public int getItemCount() {
        return newsList != null ? newsList.size() : 0;
    }

    /**
     * Update adapter data and refresh UI
     */
    public void updateData(List<NewsArticle> newList) {
        this.newsList = newList;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class
     * Holds references to item_news views
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, description;
        ImageView image, btnSave, btnShare;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.articleTitle);
            description = itemView.findViewById(R.id.articleDescription);
            image = itemView.findViewById(R.id.articleImage);
            btnSave = itemView.findViewById(R.id.btnSave);
            btnShare = itemView.findViewById(R.id.btnShare);
        }
    }
}
