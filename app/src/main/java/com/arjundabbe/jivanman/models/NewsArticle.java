package com.arjundabbe.jivanman.models;

import java.util.List;

/**
 * NewsArticle Model
 * -----------------
 * This class represents a single news item in the app.
 * It is used across RecyclerView, Firestore, and Detail screens.
 */
public class NewsArticle {

    // Unique ID of the news (Firestore document ID)
    private String newsId;

    // News title
    private String title;

    // Full news description/content
    private String description;

    // Image URL (from Firebase / server)
    private String imageUrl;

    // Drawable image resource (used for offline/static data)
    private int imageResId;

    // Date & time of news publish
    private String dateTime;

    // Reporter / Author name
    private String reporter;

    // News categories (Politics, Sports, Local, etc.)
    private List<String> category;

    /**
     * Constructor used for Firebase / Firestore news
     * (Most important constructor)
     */
    public NewsArticle(
            String newsId,
            String title,
            String description,
            String imageUrl,
            String dateTime,
            String reporter,
            List<String> category
    ) {
        this.newsId = newsId;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.imageResId = 0;      // Not using drawable
        this.dateTime = dateTime;
        this.reporter = reporter;
        this.category = category;
    }

    /**
     * Existing constructor (Backward compatibility)
     * Used where newsId or category is not required
     */
    public NewsArticle(
            String title,
            String description,
            String imageUrl,
            String dateTime,
            String reporter
    ) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.dateTime = dateTime;
        this.reporter = reporter;
        this.imageResId = 0;
    }

    /**
     * Constructor for drawable-based images
     * (Offline data / testing)
     */
    public NewsArticle(
            String title,
            String description,
            int imageResId,
            String dateTime,
            String reporter
    ) {
        this.title = title;
        this.description = description;
        this.imageResId = imageResId;
        this.dateTime = dateTime;
        this.reporter = reporter;
    }

    // -------------------------
    // Getter methods
    // -------------------------

    public String getNewsId() {
        return newsId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getReporter() {
        return reporter;
    }

    public List<String> getCategory() {
        return category;
    }
}
