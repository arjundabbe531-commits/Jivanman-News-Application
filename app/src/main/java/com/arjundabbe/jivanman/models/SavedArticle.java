package com.arjundabbe.jivanman.models;

public class SavedArticle {
    private String title;
    private String description;
    private String imageUrl;

    public SavedArticle(String title, String description, String imageUrl) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
}
