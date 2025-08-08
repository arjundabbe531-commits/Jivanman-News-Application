package com.arjundabbe.jivanman.models;

public class SavedArticle {
    private String title;
    private String description;
    private String imageUrl;
    private String date; // 🆕 Add this

    public SavedArticle(String title, String description, String imageUrl, String date) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.date = date; // 🆕
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getDate() { return date; } // 🆕
}
