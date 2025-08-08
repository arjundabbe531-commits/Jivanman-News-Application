package com.arjundabbe.jivanman.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arjundabbe.jivanman.R;
import com.arjundabbe.jivanman.adapters.SavedArticleAdapter;
import com.arjundabbe.jivanman.models.SavedArticle;
import com.arjundabbe.jivanman.util.SavedPrefsManager;

import java.util.ArrayList;

public class SavedFragment extends Fragment {

    // RecyclerView to display saved articles
    RecyclerView recyclerView;

    // Called when the fragment view is being created
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate the fragment layout (XML) and get the root view
        View view = inflater.inflate(R.layout.fragment_saved, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewSaved);

        // Set layout manager to arrange items vertically
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Get the saved articles from SharedPreferences using utility class
        ArrayList<SavedArticle> savedList = SavedPrefsManager.getSavedArticles(requireContext());

        // Create adapter with the list of saved articles
        SavedArticleAdapter adapter = new SavedArticleAdapter(requireContext(), savedList);

        // Set adapter to RecyclerView so it shows the list
        recyclerView.setAdapter(adapter);

        // Return the inflated view to be shown on screen
        return view;
    }
}
