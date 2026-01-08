package com.arjundabbe.jivanman.models;

import java.util.List;

/**
 * Model class for saved/bookmarked news articles.
 * Used in SavedFragment and SavedArticleAdapter.
 */
public class SavedArticle {

    private String newsId;
    private String title;
    private String description;
    private String imageUrl;
    private String date;
    private String reporter;
    private List<String> category;

    /**
     * Empty constructor required for Firestore deserialization
     */
    public SavedArticle() { }

    /**
     * Full constructor for creating a SavedArticle object
     * @param newsId Unique ID of the news
     * @param title Title of the article
     * @param description Description/content
     * @param imageUrl URL of the article image
     * @param date Published date as string
     * @param reporter Reporter name
     * @param category List of categories the article belongs to
     */
    public SavedArticle(String newsId, String title, String description, String imageUrl,
                        String date, String reporter, List<String> category) {
        this.newsId = newsId;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.date = date;
        this.reporter = reporter;
        this.category = category;
    }

    // ---------------- GETTERS ----------------
    public String getNewsId() { return newsId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getDate() { return date; }
    public String getReporter() { return reporter; }
    public List<String> getCategory() { return category; }

    // ---------------- SETTERS ----------------
    public void setNewsId(String newsId) { this.newsId = newsId; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setDate(String date) { this.date = date; }
    public void setReporter(String reporter) { this.reporter = reporter; }
    public void setCategory(List<String> category) { this.category = category; }
}
