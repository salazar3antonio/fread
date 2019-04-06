package com.freadapp.fread.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import com.freadapp.fread.R;
import com.freadapp.fread.data.database.FirebaseUtils;
import com.freadapp.fread.data.model.Article;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

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
    private ArrayList<Article> mArticles = new ArrayList<>();

    public ArticleWidgetRemoteViewsFactory(Context context, Intent intent) {
        this.mContext = context;
    }

    @Override
    public void onCreate() {

        Log.i(TAG, "onCreate: ");

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();

        initArticles();
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        if (mArticles.size() == 0) return 0;
        return mArticles.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        Log.i(TAG, "getViewAt: ");

        if (mArticles == null || mArticles.size() == 0) return null;

        Article mArticle = mArticles.get(position);

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

    private void initArticles() {

        if (mUser == null) {
            Toast.makeText(mContext, "No user logged in.", Toast.LENGTH_LONG).show();
        } else {
            DatabaseReference userArticles = FirebaseUtils.getUserArticles();
            userArticles.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Article article = child.getValue(Article.class);
                        mArticles.add(article);
                    }

                    updateWidgetArticles();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void updateWidgetArticles() {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(mContext, ArticleWidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.gv_article_widget);

    }

}
