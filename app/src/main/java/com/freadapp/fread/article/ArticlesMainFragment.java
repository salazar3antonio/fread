package com.freadapp.fread.article;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.freadapp.fread.R;
import com.freadapp.fread.data.database.FirebaseUtils;
import com.freadapp.fread.data.model.Article;
import com.freadapp.fread.view_holders.ArticleViewHolder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


/**
 * Fragment class should populate the Firebase Database Recyclerview Feed of all the user articles
 */

public class ArticlesMainFragment extends Fragment {

    public static final String TAG = ArticlesMainFragment.class.getName();
    public static final String ARTICLE_MODEL = "article_model";

    private DatabaseReference mUserArticles;
    private RecyclerView mArticleRecyclerView;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private TextView mEmptyArticlesView;

    public static ArticlesMainFragment newInstance() {
        return new ArticlesMainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //inflate fragment layout of article feed
        View view = inflater.inflate(R.layout.main_articles_fragment, container, false);

        mArticleRecyclerView = view.findViewById(R.id.rv_articles_main_list);
        mEmptyArticlesView = view.findViewById(R.id.tv_empty_articles);

        //check to see if user is logged in.
        if (FirebaseUtils.isFirebaseUserSignedIn()) {
            mUserArticles = FirebaseUtils.getUserArticles();
            setMainArticlesAdapter();
        } else {
            Toast.makeText(getContext(), "No user logged in.", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void setMainArticlesAdapter() {

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Article, ArticleViewHolder>(Article.class, R.layout.article_list_item,
                ArticleViewHolder.class, mUserArticles) {

            @Override
            protected void populateViewHolder(ArticleViewHolder viewHolder, final Article article, final int position) {

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //launch a new detailed article activity passing the article at the clicked position through an intent
                        Intent intent = new Intent(getContext(), ArticleDetailActivity.class);
                        intent.putExtra(ARTICLE_MODEL, article);
                        startActivity(intent);
                    }
                });

                viewHolder.bindToArticle(getContext(), article);

            }
        };

        checkForEmptyArticles(mUserArticles);

        mArticleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mArticleRecyclerView.setAdapter(mFirebaseAdapter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mFirebaseAdapter != null) {
            mFirebaseAdapter.cleanup();
        }
    }

    private void checkForEmptyArticles(DatabaseReference articles) {

        articles.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mEmptyArticlesView.setVisibility(View.GONE);
                    Log.i(TAG, "onDataChange: dataSnapshot does exits");
                } else {
                    mEmptyArticlesView.setVisibility(View.VISIBLE);
                    Log.i(TAG, "onDataChange: dataSnapshot does not exits");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
