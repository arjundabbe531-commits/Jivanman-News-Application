package com.arjundabbe.jivanman.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arjundabbe.jivanman.R;
import com.arjundabbe.jivanman.adapters.SavedArticleAdapter;
import com.arjundabbe.jivanman.models.SavedArticle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment to display all saved/bookmarked news articles
 * Data source: users/{uid}/savedNews (Firestore)
 */
public class SavedFragment extends Fragment {

    // UI
    private RecyclerView recyclerView;

    // Adapter & data list
    private SavedArticleAdapter adapter;
    private List<SavedArticle> savedList = new ArrayList<>();

    // Firebase
    private FirebaseFirestore db;
    private String uid;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        View view = inflater.inflate(R.layout.fragment_saved, container, false);

        // RecyclerView setup
        recyclerView = view.findViewById(R.id.recyclerViewSaved);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Firebase initialization
        db = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getUid();

        /**
         * Adapter initialization
         * Delete listener passed from fragment to adapter
         */
        adapter = new SavedArticleAdapter(
                requireContext(),
                savedList,
                (article, position) -> deleteArticle(article, position)
        );

        recyclerView.setAdapter(adapter);

        // Load saved articles
        loadSavedNews();

        // Enable swipe-to-delete feature
        setupSwipeToDelete();

        return view;
    }

    /**
     * Fetch all saved news from Firestore
     */
    private void loadSavedNews() {

        db.collection("users")
                .document(uid)
                .collection("savedNews")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(query -> {

                    savedList.clear();

                    for (DocumentSnapshot doc : query) {
                        savedList.add(new SavedArticle(
                                doc.getString("newsId"),
                                doc.getString("title"),
                                doc.getString("description"),
                                doc.getString("imageUrl"),
                                "", // date not used here
                                doc.getString("reporter"),
                                (List<String>) doc.get("category")
                        ));
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                getContext(),
                                "Error loading saved news",
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    /**
     * Delete saved article from Firestore and UI
     */
    private void deleteArticle(SavedArticle article, int position) {

        db.collection("users")
                .document(uid)
                .collection("savedNews")
                .document(article.getNewsId())
                .delete()
                .addOnSuccessListener(aVoid -> {

                    savedList.remove(position);
                    adapter.notifyItemRemoved(position);

                    Toast.makeText(
                            getContext(),
                            "लेख हटविला गेला",
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                getContext(),
                                "Error deleting article",
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    /**
     * Enable swipe left/right to delete saved article
     */
    private void setupSwipeToDelete() {

        ItemTouchHelper.SimpleCallback simpleCallback =
                new ItemTouchHelper.SimpleCallback(
                        0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
                ) {

                    @Override
                    public boolean onMove(
                            @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            @NonNull RecyclerView.ViewHolder target
                    ) {
                        return false;
                    }

                    @Override
                    public void onSwiped(
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            int direction
                    ) {
                        int position = viewHolder.getAdapterPosition();
                        SavedArticle article = savedList.get(position);

                        // Reuse delete function
                        deleteArticle(article, position);
                    }
                };

        new ItemTouchHelper(simpleCallback)
                .attachToRecyclerView(recyclerView);
    }
}
