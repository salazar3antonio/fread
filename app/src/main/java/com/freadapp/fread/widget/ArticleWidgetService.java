package com.freadapp.fread.widget;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import com.freadapp.fread.R;
import com.freadapp.fread.data.model.Article;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ArticleWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ArticleWidgetRemoteViewsFactory(this.getApplicationContext(), intent);
    }

}

class ArticleWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = ArticleWidgetService.class.getSimpleName();
    //this class will bind data to one Article widget view. StackView is the container for each Recipe Widget Views
    private Context mContext;
    private FirebaseUser mUser;
    private DatabaseReference mArticlesDBref;
    private ArrayList<Article> mArticles = new ArrayList<>();
    private Article mArticle;

    public ArticleWidgetRemoteViewsFactory(Context context, Intent intent) {
        this.mContext = context;
    }

    @Override
    public void onCreate() {

        Log.i(TAG, "onCreate: ");

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();

        if (mUser == null) {
            Toast.makeText(mContext, "No user logged in.", Toast.LENGTH_SHORT).show();
        } else {
            //grab an instance of the database and point it to the logged in user's articles
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            mArticlesDBref = firebaseDatabase.getReference().child("users").child(mUser.getUid()).child("articles");
            mArticlesDBref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    fetchArticles(dataSnapshot);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    fetchArticles(dataSnapshot);
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

    }

    @Override
    public void onDataSetChanged() {

        if (mUser == null) {
            Toast.makeText(mContext, "No user logged in.", Toast.LENGTH_SHORT).show();
        } else {
            //grab an instance of the database and point it to the logged in user's articles
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            mArticlesDBref = firebaseDatabase.getReference().child("users").child(mUser.getUid()).child("articles");
            mArticlesDBref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    fetchArticles(dataSnapshot);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    fetchArticles(dataSnapshot);
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

    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: ");
    }

    @Override
    public int getCount() {
        Log.i(TAG, "getCount: ");
        if (mArticles.size() == 0) return 0;
        return mArticles.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        Log.i(TAG, "getViewAt: ");

        if (mArticles == null || mArticles.size() == 0) return null;

        mArticle = mArticles.get(position);

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_article);

        remoteViews.setTextViewText(R.id.tv_widget_article_headline, mArticle.getTitle());
        remoteViews.setTextViewText(R.id.tv_widget_article_body, mArticle.getArticle());

        return remoteViews;

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    private void fetchArticles(DataSnapshot dataSnapshot) {

        Article article = dataSnapshot.getValue(Article.class);
        Log.i(TAG, "Titles: " + article.getTitle());
        mArticles.add(article);

    }


}
