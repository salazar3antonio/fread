package com.freadapp.fread;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.freadapp.fread.data.model.Article;
import com.freadapp.fread.data.model.Articles;
import com.freadapp.fread.helpers.Constants;
import com.freadapp.fread.signin_classes.SignInActivity;
import com.freadapp.fread.view_holder.ArticleViewHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getName();


    private Button mSignInButton;
    private FirebaseUser mUser;
    private DatabaseReference mArticlesDBref;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSignInButton = findViewById(R.id.signin_button);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();

        mRecyclerView = findViewById(R.id.article_recycleView);
        mRecyclerView.setHasFixedSize(true);

        //grab and instance of the database and point it to the logged in user's articles
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mArticlesDBref = firebaseDatabase.getReferenceFromUrl(Constants.ARTICLES_REFERENCE);

        mArticlesDBref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final ArrayList<Article> articles = new ArrayList<Article>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    articles.add(snapshot.getValue(Article.class));
                    Log.i(TAG, "Article Titles: " + snapshot.getValue(Article.class).getTitle());
                }

                Log.i(TAG, "Number of Articles: " + articles.size());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        setFirebaseAdapter();

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
            }
        });
    }


    private void setFirebaseAdapter() {

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Article, ArticleViewHolder>(Article.class, R.layout.article_list_item,
                ArticleViewHolder.class, mArticlesDBref) {

            @Override
            protected void populateViewHolder(ArticleViewHolder viewHolder, Article model, int position) {
                viewHolder.bindToArticle(model);
            }
        };

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mFirebaseAdapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFirebaseAdapter.cleanup();
    }
}
