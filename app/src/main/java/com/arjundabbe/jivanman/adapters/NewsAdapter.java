package com.arjundabbe.jivanman.adapters;

import android.content.Context;
import android.content.Intent;
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

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private Context context;
    private List<NewsArticle> newsList;

    public NewsAdapter(Context context, List<NewsArticle> newsList) {
        this.context = context;
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewsArticle article = newsList.get(position);

        holder.title.setText(article.getTitle());
        holder.description.setText(article.getDescription().isEmpty() ? " " : article.getDescription());
        holder.dateTime.setText(article.getDateTime());

        if (article.getImageUrl() != null && !article.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(article.getImageUrl())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(holder.image);
        } else if (article.getImageResId() != 0) {
            Glide.with(context)
                    .load(article.getImageResId())
                    .placeholder(R.drawable.placeholder)
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.placeholder);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NewsDetailActivity.class);
            intent.putExtra("title", article.getTitle());
            intent.putExtra("description", article.getDescription());
            intent.putExtra("imageUrl", article.getImageUrl());
            intent.putExtra("date", article.getDateTime());
            context.startActivity(intent);
        });




    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }
    public void updateData(List<NewsArticle> newList) {
        this.newsList = newList;
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description,dateTime;
        ImageView image, btnSave, btnShare;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.articleTitle);
            description = itemView.findViewById(R.id.articleDescription);
            dateTime = itemView.findViewById(R.id.articleDateTime);
            image = itemView.findViewById(R.id.articleImage);
            btnSave = itemView.findViewById(R.id.btnSave);
            btnShare = itemView.findViewById(R.id.btnShare);
        }
    }
}
