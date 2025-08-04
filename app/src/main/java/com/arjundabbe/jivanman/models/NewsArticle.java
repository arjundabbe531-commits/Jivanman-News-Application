package com.arjundabbe.jivanman.models;

public class NewsArticle {
    private String title;
    private String description;
    private String imageUrl;  // for web URL
    private int imageResId;   // for drawable

    private String dateTime;

    // Constructor for image URL
    public NewsArticle(String title, String description, String imageUrl,  String dateTime) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.imageResId = 0;
        this.dateTime = dateTime;
    }

    // Constructor for drawable image
    public NewsArticle(String title, String description, int imageResId, String dateTime) {
        this.title = title;
        this.description = description;
        this.imageUrl = null;
        this.imageResId = imageResId;
        this.dateTime = dateTime;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public int getImageResId() { return imageResId; }
    public String getDateTime() { return dateTime; }
}
