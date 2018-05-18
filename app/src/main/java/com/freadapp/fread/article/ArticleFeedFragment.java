package com.freadapp.fread.article;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.freadapp.fread.R;
import com.freadapp.fread.data.model.Article;
import com.freadapp.fread.view_holders.ArticleViewHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * Created by salaz on 3/11/2018.
 * fragment class should populate the Firebase Database Recyclerview Feed of all the user articles
 */

public class ArticleFeedFragment extends Fragment {

    public static final String TAG = ArticleFeedFragment.class.getName();
    public static final String FB_ARTICLE_KEY_ID = "fb_key_id";
    public static final String ARTICLE_MODEL = "article_model";


    private FirebaseUser mUser;
    private DatabaseReference mArticlesDBref;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private RecyclerView mRecyclerView;

    //todo tony move this class to List folder. This class in not an article. It populates all User Articles

    public static ArticleFeedFragment newInstance() {
        return new ArticleFeedFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //inflate fragment layout of article feed
        View view = inflater.inflate(R.layout.article_feed_fragment, container, false);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();

        mRecyclerView = view.findViewById(R.id.article_recycleView);

        //check to see if user is logged in.
        if (mUser == null) {
            Toast.makeText(getContext(), "No user logged in.", Toast.LENGTH_SHORT).show();
        } else {
            //grab an instance of the database and point it to the logged in user's articles
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            mArticlesDBref = firebaseDatabase.getReference().child("users").child(mUser.getUid()).child("articles");
            mArticlesDBref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Article article = dataSnapshot.getValue(Article.class);
                    Log.i(TAG, "Article Title: " + article.getTitle());
                    Log.i(TAG, "User UID: " + article.getUid());
                    setFirebaseAdapter();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

        return view;
    }

    private void setFirebaseAdapter() {

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Article, ArticleViewHolder>(Article.class, R.layout.article_list_item,
                ArticleViewHolder.class, mArticlesDBref) {

            @Override
            protected void populateViewHolder(ArticleViewHolder viewHolder, final Article model, final int position) {

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //launch a new detailed article activity passing the article at the clicked position through an intent
                        Intent intent = new Intent(getContext(), ArticleDetailActivity.class);
                        intent.putExtra(FB_ARTICLE_KEY_ID, model.getKeyid());
                        intent.putExtra(ARTICLE_MODEL, model);
                        startActivity(intent);
                    }
                });

                Context context = getContext();
                viewHolder.bindToArticle(model, context);

            }
        };

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mFirebaseAdapter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mFirebaseAdapter != null) {
            mFirebaseAdapter.cleanup();
        }
    }
}
