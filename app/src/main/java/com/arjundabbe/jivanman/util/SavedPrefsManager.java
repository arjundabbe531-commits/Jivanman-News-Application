package com.arjundabbe.jivanman.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.arjundabbe.jivanman.models.SavedArticle;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SavedPrefsManager {
    private static final String PREF_NAME = "saved_articles";
    private static final String KEY_LIST = "article_list";

    public static void saveArticle(Context context, SavedArticle article) {
        ArrayList<SavedArticle> list = getSavedArticles(context);
        list.add(article);
        saveList(context, list);
    }

    public static ArrayList<SavedArticle> getSavedArticles(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_LIST, "");
        Type type = new TypeToken<ArrayList<SavedArticle>>() {}.getType();
        return json.isEmpty() ? new ArrayList<>() : new Gson().fromJson(json, type);
    }
    public static void removeArticle(Context context, SavedArticle articleToRemove) {
        ArrayList<SavedArticle> savedList = getSavedArticles(context);
        savedList.removeIf(article -> article.getTitle().equals(articleToRemove.getTitle()));
        saveList(context, savedList); // ✅ Not saveArticleList()
    }


    private static void saveList(Context context, ArrayList<SavedArticle> list) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        String json = new Gson().toJson(list);
        editor.putString(KEY_LIST, json);
        editor.apply();
    }
}
