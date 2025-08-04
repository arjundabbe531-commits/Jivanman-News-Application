package com.arjundabbe.jivanman.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arjundabbe.jivanman.R;
import com.arjundabbe.jivanman.adapters.NewsAdapter;
import com.arjundabbe.jivanman.models.NewsArticle;
import com.arjundabbe.jivanman.util.RSSFeedParser;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private NewsAdapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.homeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        new Thread(() -> {
            RSSFeedParser parser = new RSSFeedParser();

            // ✅ Marathi RSS feed URLs (replace or add more if needed)
            String[] feedUrls = {
                    "https://www.loksatta.com/maharashtra/feed/",
                    "https://www.loksatta.com/krida/feed/"

            };


            List<NewsArticle> allArticles = new ArrayList<>();

            for (String url : feedUrls) {
                List<NewsArticle> articles = parser.parse(url);
                allArticles.addAll(articles);
            }

            requireActivity().runOnUiThread(() -> {
                adapter = new NewsAdapter(getActivity(), allArticles);
                recyclerView.setAdapter(adapter);
            });

        }).start();

        return view;
    }
}
