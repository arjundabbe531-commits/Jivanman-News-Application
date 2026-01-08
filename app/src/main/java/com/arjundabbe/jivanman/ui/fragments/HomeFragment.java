package com.arjundabbe.jivanman.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arjundabbe.jivanman.R;
import com.arjundabbe.jivanman.adapters.NewsAdapter;
import com.arjundabbe.jivanman.models.NewsArticle;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * HomeFragment
 * -------------
 * Displays the latest news on the Home screen.
 * Fetches news data from Firestore and shows it
 * in a RecyclerView using NewsAdapter.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    // UI components
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    // Adapter & data
    private NewsAdapter adapter;
    private List<NewsArticle> newsList;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {

        Log.d(TAG, "onCreateView: Fragment created");

        // Inflate fragment layout
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize UI elements
        recyclerView = view.findViewById(R.id.homeRecyclerView);
        progressBar = view.findViewById(R.id.homeProgressBar);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Initialize data list
        newsList = new ArrayList<>();

        // Setup adapter
        adapter = new NewsAdapter(getActivity(), newsList);
        recyclerView.setAdapter(adapter);

        // Fetch news data from Firestore
        fetchNewsFromFirestore();

        return view;
    }

    /**
     * Fetch news articles from Firestore database
     * Only non-deleted news is fetched and
     * ordered by latest timestamp first.
     */
    private void fetchNewsFromFirestore() {

        Log.d(TAG, "fetchNewsFromFirestore: Fetching news...");
        progressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("News")
                .whereEqualTo("isDeleted", false)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    progressBar.setVisibility(View.GONE);
                    newsList.clear();

                    Log.d(TAG, "Fetched documents: " + queryDocumentSnapshots.size());

                    // Loop through fetched documents
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                        Log.d(TAG, "Processing document ID: " + doc.getId());

                        // Fetch fields from Firestore document
                        String title = doc.getString("title");
                        String description = doc.getString("description");
                        String imageUrl = doc.getString("imageUrl");
                        String reporter = doc.getString("reporter");

                        // Handle timestamp conversion
                        Timestamp timestamp = doc.getTimestamp("timestamp");
                        String dateTime;

                        if (timestamp != null) {
                            Date date = timestamp.toDate();
                            dateTime = new SimpleDateFormat(
                                    "dd MMM yyyy, HH:mm",
                                    Locale.getDefault()
                            ).format(date);
                        } else {
                            Log.w(TAG, "Timestamp null for ID: " + doc.getId());
                            dateTime = "तारीख उपलब्ध नाही";
                        }

                        // Null / empty safety for text fields
                        if (title == null || title.trim().isEmpty()) {
                            title = "शीर्षक उपलब्ध नाही";
                        }

                        if (description == null || description.trim().isEmpty()) {
                            description = "वर्णन उपलब्ध नाही";
                        }

                        if (reporter == null || reporter.trim().isEmpty()) {
                            reporter = "जिवनमान टीम";
                        }

                        // Category list (Marathi categories)
                        List<String> category =
                                (List<String>) doc.get("category");

                        // Create NewsArticle model
                        NewsArticle article = new NewsArticle(
                                doc.getId(),   // News ID
                                title,
                                description,
                                imageUrl,
                                dateTime,
                                reporter,
                                category
                        );

                        // Add to list
                        newsList.add(article);
                        Log.d(TAG, "Added article -> " + title);
                    }

                    // Update RecyclerView adapter
                    adapter.updateData(newsList);
                    Log.d(TAG, "Adapter updated, total articles: " + newsList.size());
                })
                .addOnFailureListener(e -> {

                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Firestore error: ", e);

                    Toast.makeText(
                            getActivity(),
                            "डेटा लोड करण्यात अयशस्वी: " + e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }
}
