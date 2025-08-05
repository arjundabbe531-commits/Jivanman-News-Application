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

    private final Context context;
    private final List<SavedArticle> articleList;

    public SavedArticleAdapter(Context context, List<SavedArticle> articleList) {
        this.context = context;
        this.articleList = articleList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image, shareButton, removeButton;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.articleTitle);
            image = itemView.findViewById(R.id.articleImage);
            shareButton = itemView.findViewById(R.id.shareButton);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }

    @Override
    public SavedArticleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_saved_article, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SavedArticleAdapter.ViewHolder holder, int position) {
        SavedArticle article = articleList.get(position);
        holder.title.setText(article.getTitle());
        Glide.with(context).load(article.getImageUrl()).into(holder.image);

        // SHARE BUTTON LOGIC
        holder.shareButton.setOnClickListener(v -> {
            String shareText = article.getTitle() + "\n\n" + article.getDescription() + "\n\nजास्त माहिती: www.jivanman.in";
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, article.getTitle());
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            context.startActivity(Intent.createChooser(shareIntent, "Share via"));
        });

        // REMOVE BUTTON LOGIC
        holder.removeButton.setOnClickListener(v -> {
            int removedPosition = holder.getAdapterPosition();
            if (removedPosition != RecyclerView.NO_POSITION) {
                SavedPrefsManager.removeArticle(context, article); // remove from SharedPreferences
                articleList.remove(removedPosition);                // remove from current list
                notifyItemRemoved(removedPosition);                // update UI
                Toast.makeText(context, "Article removed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }
}
