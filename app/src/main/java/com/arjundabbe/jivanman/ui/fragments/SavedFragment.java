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

    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewSaved);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ArrayList<SavedArticle> savedList = SavedPrefsManager.getSavedArticles(requireContext());
        SavedArticleAdapter adapter = new SavedArticleAdapter(requireContext(), savedList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
