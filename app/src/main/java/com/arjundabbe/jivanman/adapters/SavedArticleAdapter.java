package com.arjundabbe.jivanman.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.arjundabbe.jivanman.R;
import com.arjundabbe.jivanman.models.SavedArticle;
import com.arjundabbe.jivanman.util.SavedPrefsManager;
import com.bumptech.glide.Glide;

import java.util.List;

public class SavedArticleAdapter extends RecyclerView.Adapter<SavedArticleAdapter.ViewHolder> {

    private final Context context; // Application or activity context
    private final List<SavedArticle> articleList; // List of saved articles to show in RecyclerView

    // Constructor: called when adapter is created
    public SavedArticleAdapter(Context context, List<SavedArticle> articleList) {
        this.context = context;
        this.articleList = articleList;
    }

    // ViewHolder class holds views for each RecyclerView item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;               // TextView for article title
        ImageView image;              // ImageView for article image
        ImageView shareButton;       // ImageView acting as share button
        ImageView removeButton;      // ImageView acting as remove/delete button

        public ViewHolder(View itemView) {
            super(itemView);

            // Bind views using their ID from the XML layout
            title = itemView.findViewById(R.id.articleTitle);
            image = itemView.findViewById(R.id.articleImage);
            shareButton = itemView.findViewById(R.id.shareButton);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }


    // Called to create new ViewHolder object for each item
    @Override
    public SavedArticleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout XML for individual saved article item
        View view = LayoutInflater.from(context).inflate(R.layout.item_saved_article, parent, false);
        return new ViewHolder(view);
    }

    // Called to bind data to the views (title, image, buttons) for each item
    @Override
    public void onBindViewHolder(SavedArticleAdapter.ViewHolder holder, int position) {
        // Get the article object at the current position
        SavedArticle article = articleList.get(position);

        // Set the article title to the TextView
        holder.title.setText(article.getTitle());

        // Load image from URL using Glide library
        Glide.with(context).load(article.getImageUrl()).into(holder.image);

        // ===== SHARE BUTTON FUNCTIONALITY =====
        holder.shareButton.setOnClickListener(v -> {
            // Create the text to be shared in Marathi
            String shareText = "लेख: " + article.getTitle() + "\n\n" +
                    article.getDescription() + "\n\n" +
                    "अधिक माहितीसाठी भेट द्या: www.jivanman.in";

            // Create an intent to share text
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, article.getTitle());
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

            // Launch chooser to let user pick app for sharing
            context.startActivity(Intent.createChooser(shareIntent, "कशाद्वारे शेअर करायचे ते निवडा"));
        });

        // ===== REMOVE BUTTON FUNCTIONALITY =====
        holder.removeButton.setOnClickListener(v -> {
            int removedPosition = holder.getAdapterPosition(); // Get position of item to be removed

            // Check to avoid invalid positions
            if (removedPosition != RecyclerView.NO_POSITION) {
                // Remove from SharedPreferences using utility class
                SavedPrefsManager.removeArticle(context, article);

                // Remove from local list and notify adapter
                articleList.remove(removedPosition);
                notifyItemRemoved(removedPosition);

                // Show confirmation toast in Marathi
                Toast.makeText(context, "लेख यादीतून काढण्यात आला आहे", Toast.LENGTH_SHORT).show();
            }
        });
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, com.arjundabbe.jivanman.ui.NewsDetailActivity.class);
            intent.putExtra("title", article.getTitle());
            intent.putExtra("description", article.getDescription());
            intent.putExtra("imageUrl", article.getImageUrl());
            intent.putExtra("date", article.getDate()); // If date is available in SavedArticle model
            context.startActivity(intent);
        });

    }

    // Total number of items in the list
    @Override
    public int getItemCount() {
        return articleList.size();
    }
}
