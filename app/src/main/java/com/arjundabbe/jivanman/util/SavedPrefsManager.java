package com.arjundabbe.jivanman.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.arjundabbe.jivanman.models.SavedArticle;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Utility class to manage saving and retrieving articles locally
 * using SharedPreferences as JSON.
 */
public class SavedPrefsManager {

    // Name of the SharedPreferences file
    private static final String PREF_NAME = "saved_articles";

    // Key for storing the saved article list
    private static final String KEY_LIST = "article_list";

    // ================= SAVE A SINGLE ARTICLE =================
    public static void saveArticle(Context context, SavedArticle article) {
        // Retrieve the existing list
        ArrayList<SavedArticle> list = getSavedArticles(context);

        // Add the new article
        list.add(article);

        // Save the updated list
        saveList(context, list);
    }

    // ================= GET SAVED ARTICLES =================
    public static ArrayList<SavedArticle> getSavedArticles(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        String json = prefs.getString(KEY_LIST, "");

        Type type = new TypeToken<ArrayList<SavedArticle>>() {}.getType();

        // Return empty list if nothing saved, otherwise parse JSON
        return json.isEmpty() ? new ArrayList<>() : new Gson().fromJson(json, type);
    }

    // ================= REMOVE ARTICLE BY TITLE =================
    public static void removeArticle(Context context, SavedArticle articleToRemove) {
        ArrayList<SavedArticle> savedList = getSavedArticles(context);

        // Remove article matching title (can be improved to use unique ID)
        savedList.removeIf(article -> article.getTitle().equals(articleToRemove.getTitle()));

        saveList(context, savedList);
    }

    // ================= PRIVATE HELPER: SAVE FULL LIST =================
    private static void saveList(Context context, ArrayList<SavedArticle> list) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();

        // Convert list to JSON
        String json = new Gson().toJson(list);

        editor.putString(KEY_LIST, json);
        editor.apply(); // save asynchronously
    }
}
