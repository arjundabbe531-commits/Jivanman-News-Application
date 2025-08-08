package com.arjundabbe.jivanman.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.arjundabbe.jivanman.models.SavedArticle;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SavedPrefsManager {

    // Name of the SharedPreferences file
    private static final String PREF_NAME = "saved_articles";

    // Key for storing the saved article list
    private static final String KEY_LIST = "article_list";

    // ============ SAVE A SINGLE ARTICLE ============
    public static void saveArticle(Context context, SavedArticle article) {
        // Get the current list from SharedPreferences
        ArrayList<SavedArticle> list = getSavedArticles(context);

        // Add the new article to the list
        list.add(article);

        // Save the updated list back to SharedPreferences
        saveList(context, list);
    }

    // ============ GET SAVED ARTICLES ============
    public static ArrayList<SavedArticle> getSavedArticles(Context context) {
        // Access SharedPreferences file
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Get the JSON string of the list from SharedPreferences
        String json = prefs.getString(KEY_LIST, "");

        // Define the type for deserialization
        Type type = new TypeToken<ArrayList<SavedArticle>>() {}.getType();

        // If no data is found, return empty list, else convert JSON to list
        return json.isEmpty() ? new ArrayList<>() : new Gson().fromJson(json, type);
    }

    // ============ REMOVE ARTICLE BY TITLE ============
    public static void removeArticle(Context context, SavedArticle articleToRemove) {
        // Get the current saved list
        ArrayList<SavedArticle> savedList = getSavedArticles(context);

        // Remove the article that matches the title
        // (can be improved to use unique ID if available)
        savedList.removeIf(article -> article.getTitle().equals(articleToRemove.getTitle()));

        // Save the updated list back
        saveList(context, savedList);
    }

    // ============ SAVE FULL LIST (PRIVATE HELPER METHOD) ============
    private static void saveList(Context context, ArrayList<SavedArticle> list) {
        // Get SharedPreferences editor to write changes
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();

        // Convert the list to a JSON string
        String json = new Gson().toJson(list);

        // Save the JSON string under the key
        editor.putString(KEY_LIST, json);

        // Apply changes asynchronously
        editor.apply();
    }
}
