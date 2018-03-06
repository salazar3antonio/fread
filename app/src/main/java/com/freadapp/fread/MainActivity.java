package com.freadapp.fread;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.freadapp.fread.article_classes.ArticleActivity;
import com.freadapp.fread.data.model.Article;
import com.freadapp.fread.helpers.Constants;
import com.freadapp.fread.signin_classes.SignInActivity;
import com.freadapp.fread.view_holder.ArticleViewHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getName();
    public static final String FB_ARTICLE = "fb_article";


    private FirebaseUser mUser;
    private DatabaseReference mArticlesDBref;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private RecyclerView mRecyclerView;
    private Query mQueryByUserArticles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();

        mRecyclerView = findViewById(R.id.article_recycleView);
        mRecyclerView.setHasFixedSize(true);

        //check to see if user is logged in.
        if (mUser == null) {
            Toast.makeText(getApplicationContext(), "No user logged in.", Toast.LENGTH_SHORT).show();
        } else {
            //grab an instance of the database and point it to the logged in user's articles
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            mArticlesDBref = firebaseDatabase.getReference().child("users").child(mUser.getUid()).child("articles");
            //query by articles of the user that is logged in
            mQueryByUserArticles = mArticlesDBref.orderByChild("uid").equalTo(mUser.getUid());
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

            setFirebaseAdapter();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate the menu view on the toolbar
        getMenuInflater().inflate(R.menu.main_menus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_in_item:
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);


        }


    }

    private void setFirebaseAdapter() {

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Article, ArticleViewHolder>(Article.class, R.layout.article_list_item,
                ArticleViewHolder.class, mQueryByUserArticles) {

            @Override
            protected void populateViewHolder(ArticleViewHolder viewHolder, final Article model, final int position) {

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //launch a new detailed article activity passing the article at the clicked position through an intent
                        Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
                        intent.putExtra(FB_ARTICLE, model);
                        startActivity(intent);
                    }
                });

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
